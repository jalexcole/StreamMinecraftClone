#include "world/ChunkManager.h"
#include "world/World.h"
#include "world/BlockMap.h"
#include "core/Pool.hpp"
#include "core/File.h"
#include "utils/DebugStats.h"
#include "utils/CMath.h"
#include "renderer/Shader.h"

namespace Minecraft
{
	enum class CommandType : uint8
	{
		FillBlockData,
		TesselateVertices
	};

	struct FillChunkCommand
	{
		// Must be at least ChunkWidth * ChunkDepth * ChunkHeight blocks available
		Block* blockData;
		Pool<SubChunk, World::ChunkCapacity * 16>* subChunks;
		glm::ivec2 chunkCoordinates;
		CommandType type;
	};

	class CompareFillChunkCommand
	{
	public:
		bool operator()(const FillChunkCommand& a, const FillChunkCommand& b)
		{
			// Tesselation is lower priority than loading block data
			int aValue = a.type == CommandType::TesselateVertices ? 0 : 1;
			int bValue = b.type == CommandType::TesselateVertices ? 0 : 1;
			return aValue < bValue;
		}
	};

	namespace ChunkManager
	{
		class ChunkWorker
		{
		public:
			ChunkWorker(int numThreads, int worldSeed)
				: cv(), mtx(), queueMtx(), doWork(true)
			{
				seed = worldSeed;
				for (int i = 0; i < numThreads; i++)
				{
					workerThreads.push_back(std::thread(&ChunkWorker::threadWorker, this));
				}
			}

			void free()
			{
				{
					std::lock_guard lock(mtx);
					doWork = false;
				}
				cv.notify_all();

				for (int i = 0; i < workerThreads.size(); i++)
				{
					workerThreads[i].join();
				}
			}

			void threadWorker()
			{
				bool shouldContinue = true;
				while (shouldContinue)
				{
					if (commands.empty())
					{
						// Wait until we need to do some work
						std::unique_lock<std::mutex> lock(mtx);
						cv.wait(lock, [&] { return !doWork || !commands.empty(); });
						shouldContinue = doWork;
					}

					FillChunkCommand command;
					bool processCommand = false;
					{
						std::lock_guard<std::mutex> queueLock(queueMtx);
						if (!commands.empty())
						{
							command = commands.top();
							commands.pop();
							processCommand = true;
						}
					}

					if (processCommand)
					{
						switch (command.type)
						{
						case CommandType::FillBlockData:
						{
							if (Chunk::exists("world", command.chunkCoordinates))
							{
								Chunk::deserialize(command.blockData, "world", command.chunkCoordinates);
							}
							else
							{
								Chunk::generate(command.blockData, command.chunkCoordinates, seed);
							}

							// Queue the tesselation commands
							command.type = CommandType::TesselateVertices;
							queueCommand(command);
							beginWork(false);
						}
						break;
						case CommandType::TesselateVertices:
						{
							Chunk::generateRenderData(command.subChunks, command.blockData, command.chunkCoordinates);
						}
						break;
						}
					}
				}
			}

			void queueCommand(const FillChunkCommand& command)
			{
				{
					std::lock_guard<std::mutex> lockGuard(queueMtx);
					commands.push(command);
				}
			}

			void beginWork(bool notifyAll = true)
			{
				if (notifyAll)
				{
					cv.notify_all();
				}
				else
				{
					cv.notify_one();
				}
			}

		private:
			std::priority_queue<FillChunkCommand, std::vector<FillChunkCommand>, CompareFillChunkCommand> commands;
			std::vector<std::thread> workerThreads;
			std::condition_variable cv;
			std::mutex mtx;
			std::mutex queueMtx;
			bool doWork;

			uint32 seed;
		};

		// Internal variables
		static std::mutex chunkMtx;
		static uint32 processorCount = 0;
		static uint32 worldSeed = 0;
		static std::bitset<World::ChunkCapacity> loadedChunks;
		static std::unordered_map<glm::ivec2, int> chunkIndices = {};

		// Singletons
		static ChunkWorker& chunkWorker()
		{
			static ChunkWorker instance(processorCount, worldSeed);
			return instance;
		}

		static Pool<SubChunk, World::ChunkCapacity * 16>& subChunks()
		{
			static Pool<SubChunk, World::ChunkCapacity * 16> instance{ 1 };
			return instance;
		}

		static Pool<Vertex, World::ChunkCapacity * 16>& vertexPool()
		{
			// 16 SubChunks per chunk
			static Pool<Vertex, World::ChunkCapacity * 16> instance(World::MaxVertsPerSubChunk);
			return instance;
		}

		static Pool<Block, World::ChunkCapacity>& blockPool()
		{
			static Pool<Block, World::ChunkCapacity> instance(World::ChunkDepth * World::ChunkWidth * World::ChunkHeight);
			return instance;
		}

		void init(uint32 seed)
		{
			// A chunk uses 55,000 vertices on average, so a sub-chunk can be estimated to use about 
			// 4,500 vertices on average. That's the default vertex bucket size
			processorCount = std::thread::hardware_concurrency();
			worldSeed = seed;

			// Initialize the singletons
			chunkWorker();
			blockPool();
			Pool<Vertex, World::ChunkCapacity * 16>& vertexPools = vertexPool();

			// Initialize the SubChunks
			loadedChunks.reset();
			for (uint32 i = 0; i < vertexPools.size(); i++)
			{
				// Assign the pointers for the data on the CPU
				subChunks()[i]->data = vertexPools[i];
				subChunks()[i]->numVertsUsed = 0;
				subChunks()[i]->state = SubChunkState::Unloaded;

				// Generate a bunch of empty vertex buckets for GPU use
				glCreateVertexArrays(1, &subChunks()[i]->vao);
				glBindVertexArray(subChunks()[i]->vao);

				glGenBuffers(1, &subChunks()[i]->vbo);

				glBindBuffer(GL_ARRAY_BUFFER, subChunks()[i]->vbo);
				glBufferData(GL_ARRAY_BUFFER, sizeof(Vertex) * vertexPools.poolSize(), NULL, GL_DYNAMIC_DRAW);

				// Set our vertex attribute pointers
				glVertexAttribIPointer(0, 1, GL_UNSIGNED_INT, sizeof(Vertex), (void*)offsetof(Vertex, data1));
				glEnableVertexAttribArray(0);

				glVertexAttribIPointer(1, 1, GL_UNSIGNED_INT, sizeof(Vertex), (void*)(offsetof(Vertex, data2)));
				glEnableVertexAttribArray(1);
			}

			// Subchunk = 16x16x16  Blocks
			// BigChunk = 16x256x16 Blocks
			g_logger_info("Vertex Pool Total Size: %2.3f Gb", (float)(vertexPool().totalSize() / (1024.0f * 1024 * 1024)));
			g_logger_info("Block Pool Total Size: %2.3f Gb", (float)(blockPool().totalSize() / (1024.0f * 1024 * 1024)));
		}

		void free()
		{
			chunkWorker().free();
		}

		void queueCreateChunk(const glm::ivec2& chunkCoordinates, bool retesselate)
		{
			// Only upload if we need to
			bool upload;
			{
				std::lock_guard<std::mutex> lock(chunkMtx);
				upload = chunkIndices.find(chunkCoordinates) == chunkIndices.end();
			}
			if (upload)
			{
				FillChunkCommand cmd;
				cmd.chunkCoordinates = chunkCoordinates;
				cmd.type = CommandType::FillBlockData;
				cmd.blockData = nullptr;
				cmd.subChunks = &(subChunks());

				for (int i = 0; i < loadedChunks.size(); i++)
				{
					if (!loadedChunks.test(i))
					{
						loadedChunks.set(i, true);
						{
							std::lock_guard<std::mutex> lock(chunkMtx);
							chunkIndices[chunkCoordinates] = i;
						}
						cmd.blockData = blockPool()[i];
						chunkWorker().queueCommand(cmd);
						g_logger_log("Queueing chunk <%d, %d>", chunkCoordinates.x, chunkCoordinates.y);
						break;
					}
				}
			}
			else if (retesselate)
			{
			//	FillChunkCommand cmd;
			//	cmd.chunkCoordinates = chunkCoordinates;
			//	cmd.type = CommandType::TesselateVertices;
			//	cmd.subChunks = &subChunks();
			//	cmd.blockData = nullptr;
			//	{
			//		std::lock_guard<std::mutex> lock(chunkMtx);
			//		auto iter = chunkIndices.find(chunkCoordinates);
			//		if (iter == chunkIndices.end())
			//		{
			//			return;
			//		}
			//		cmd.blockData = blockPool()[iter->second];
			//	}

			//	for (int i = 0; i < subChunks().size(); i++)
			//	{
			//		if (subChunks()[i]->chunkCoordinates == chunkCoordinates && subChunks()[i]->state == SubChunkState::Uploaded)
			//		{
			//			subChunks()[i]->state = SubChunkState::RetesselateVertices;
			//			chunkWorker().queueCommand(cmd);
			//		}
			//	}
			}
		}

		void processCommands()
		{
			chunkWorker().beginWork();
		}

		Block getBlock(const glm::vec3& worldPosition)
		{
			glm::ivec2 chunkCoords = World::toChunkCoords(worldPosition);
			Block* blockData = nullptr;

			std::lock_guard<std::mutex> lock(chunkMtx);
			auto iter = chunkIndices.find(chunkCoords);
			if (iter != chunkIndices.end())
			{
				blockData = blockPool()[iter->second];
			}
			else if (worldPosition.y >= 0 && worldPosition.y < 256)
			{
				// Assume it's a chunk that's out of bounds
				// TODO: Make this only return air block if it's far far away from the player
				return BlockMap::AIR_BLOCK;
			}

			return Chunk::getBlock(worldPosition, chunkCoords, blockData);
		}

		void render(const glm::vec3& playerPosition, const glm::ivec2& playerPositionInChunkCoords, Shader& shader)
		{
			// TODO: Remove me, this is for debugging purposes
			static uint32 maxVertCount = 0;
			static uint32 minVertCount = UINT32_MAX;
			static uint32 last100Verts[100] = {};
			int last100VertsIndex = 0;
			float avgVertCount = 0.0f;
			for (int i = 0; i < 100; i++)
			{
				avgVertCount += last100Verts[i];
			}
			if (avgVertCount > 0)
			{
				avgVertCount /= 100;
			}
			DebugStats::minVertCount = minVertCount;
			DebugStats::maxVertCount = maxVertCount;
			DebugStats::avgVertCount = avgVertCount;

			// TODO: Weird that I have to re-enable that here. Try to find out why?
			glEnable(GL_CULL_FACE);

			for (int i = 0; i < subChunks().size(); i++)
			{
				if (subChunks()[i]->state != SubChunkState::Unloaded)
				{
					glm::ivec2 chunkPos = subChunks()[i]->chunkCoordinates;
					auto iter = chunkIndices.find(chunkPos);
					if (iter == chunkIndices.end() && subChunks()[i]->state != SubChunkState::TesselatingVertices)
					{
						// If the chunk coords are no longer loaded, set this chunk as not in use anymore
						subChunks()[i]->state = SubChunkState::Unloaded;
						subChunks()[i]->numVertsUsed = 0;
						subChunks().freePool(i);
					}
					else if (iter != chunkIndices.end() && loadedChunks.test(iter->second))
					{
						// Otherwise render it
						shader.uploadIVec2("uChunkPos", chunkPos);
						shader.uploadVec3("uPlayerPosition", playerPosition);
						shader.uploadInt("uChunkRadius", World::ChunkRadius);

						if (subChunks()[i]->state == SubChunkState::UploadVerticesToGpu)
						{
							g_logger_assert(subChunks()[i]->numVertsUsed.load() > 0, "Sub Chunk should never have tried to upload 0 verts to GPU.");
							glBindBuffer(GL_ARRAY_BUFFER, subChunks()[i]->vbo);
							glBufferSubData(GL_ARRAY_BUFFER, 0, subChunks()[i]->numVertsUsed * sizeof(Vertex), subChunks()[i]->data);
							subChunks()[i]->state = SubChunkState::Uploaded;

							// TODO: Remove me this is for debugging purposes
							last100Verts[last100VertsIndex] = subChunks()[i]->numVertsUsed;
							maxVertCount = glm::max(subChunks()[i]->numVertsUsed.load(), maxVertCount);
							minVertCount = glm::min(subChunks()[i]->numVertsUsed.load(), minVertCount);
							last100VertsIndex = (last100VertsIndex + 1) % 100;
						}

						if (subChunks()[i]->state == SubChunkState::Uploaded || subChunks()[i]->state == SubChunkState::RetesselateVertices)
						{
							g_logger_assert(subChunks()[i]->numVertsUsed.load() > 0, "Sub Chunk should never have tried to upload 0 verts to GPU.");
							glBindVertexArray(subChunks()[i]->vao);
							glDrawArrays(GL_TRIANGLES, 0, subChunks()[i]->numVertsUsed);
							DebugStats::numDrawCalls++;
						}

						const glm::ivec2 localChunkPos = chunkPos - playerPositionInChunkCoords;
						bool inRangeOfPlayer = (localChunkPos.x * localChunkPos.x) + (localChunkPos.y * localChunkPos.y) <=
							(World::ChunkRadius * World::ChunkRadius);
						if (!inRangeOfPlayer)
						{
							g_logger_log("Deleting chunk<%d, %d> Local position<%d, %d>", chunkPos.x, chunkPos.y, localChunkPos.x, localChunkPos.y);
							int chunkIndex = iter->second;
							loadedChunks.set(chunkIndex, false);
							std::lock_guard lock(chunkMtx);
							chunkIndices.erase(iter);
							subChunks()[i]->state = SubChunkState::Unloaded;
							subChunks()[i]->numVertsUsed = 0;
						}
					}
				}
			}
		}
	}

	namespace Chunk
	{
		// Internal Enums
		enum class CUBE_FACE : uint32
		{
			LEFT = 0,
			RIGHT = 1,
			BOTTOM = 2,
			TOP = 3,
			BACK = 4,
			FRONT = 5
		};

		enum class UV_INDEX : uint32
		{
			TOP_RIGHT = 0,
			TOP_LEFT = 1,
			BOTTOM_LEFT = 2,
			BOTTOM_RIGHT = 3
		};

		// Internal Constants
		static const int POSITION_INDEX_BITMASK = 0x1FFFF;
		static const int TEX_ID_BITMASK = 0x1FFE0000;
		static const int FACE_BITMASK = 0xE0000000;
		static const int UV_INDEX_BITMASK = 0x3;

		static const int BASE_17_DEPTH = 17;
		static const int BASE_17_WIDTH = 17;
		static const int BASE_17_HEIGHT = 289;

		// Internal functions
		static int to1DArray(int x, int y, int z);
		static Block getBlockInternal(const Block* data, int x, int y, int z, const glm::ivec2& chunkCoordinates);
		static std::string getFormattedFilepath(const glm::ivec2& chunkCoordinates, const std::string& worldSavePath);
		static void loadBlock(Vertex* vertexData, const glm::ivec3& vert1, const glm::ivec3& vert2, const glm::ivec3& vert3, const glm::ivec3& vert4, const TextureFormat& texture, CUBE_FACE face);

		void info()
		{
			g_logger_info("%d size of chunk", sizeof(Block) * World::ChunkWidth * World::ChunkDepth * World::ChunkHeight);
			g_logger_info("Max %d size of vertex data", sizeof(Vertex) * World::ChunkWidth * World::ChunkHeight * World::ChunkDepth * 24);
		}

		void generate(Block* blockData, const glm::ivec2& chunkCoordinates, int32 seed)
		{
			const int worldChunkX = chunkCoordinates.x * 16;
			const int worldChunkZ = chunkCoordinates.y * 16;

			// TODO: Should we zero the memory in release mode as well? Or does it matter?
			g_memory_zeroMem(blockData, sizeof(Block) * World::ChunkWidth * World::ChunkHeight * World::ChunkDepth);
			const SimplexNoise generator = SimplexNoise();
			for (int y = 0; y < World::ChunkHeight; y++)
			{
				for (int x = 0; x < World::ChunkDepth; x++)
				{
					for (int z = 0; z < World::ChunkWidth; z++)
					{
						// 24 Vertices per cube
						const float incrementSize = 1000.0f;
						const int arrayExpansion = to1DArray(x, y, z);
						float maxHeightFloat =
							CMath::mapRange(
								generator.fractal(
									7,
									(x + worldChunkX + seed) / incrementSize,
									(z + worldChunkZ + seed) / incrementSize
								),
								-1.0f,
								1.0f,
								0.0f,
								1.0f
							) * 255.0f;
						int16 maxHeight = (int16)maxHeightFloat;

						float stoneHeightFloat =
							CMath::mapRange(
								generator.fractal(
									7,
									(x + worldChunkX + seed) / incrementSize,
									(z + worldChunkZ + seed) / incrementSize
								),
								-1.0f,
								1.0f,
								0.0f,
								1.0f
							) * 255.0f;
						int16 stoneHeight = (int16)((stoneHeightFloat * maxHeightFloat) / 127.0f);

						if (y == 0)
						{
							// Bedrock
							blockData[arrayExpansion].id = 7;
						}
						else if (y < stoneHeight)
						{
							// Stone
							blockData[arrayExpansion].id = 6;
						}
						else if (y < maxHeight)
						{
							// Dirt
							blockData[arrayExpansion].id = 4;
						}
						else if (y == maxHeight)
						{
							// Green Concrete 
							blockData[arrayExpansion].id = 5;
						}
					}
				}
			}
		}

		Block getLocalBlock(const glm::ivec3& localPosition, const glm::ivec2& chunkCoordinates, const Block* blockData)
		{
			return getBlockInternal(blockData, localPosition.x, localPosition.y, localPosition.z, chunkCoordinates);
		}

		Block getBlock(const glm::vec3& worldPosition, const glm::ivec2& chunkCoordinates, const Block* blockData)
		{
			glm::ivec3 localPosition = glm::floor(worldPosition - glm::vec3(chunkCoordinates.x * 16.0f, 0.0f, chunkCoordinates.y * 16.0f));
			return getLocalBlock(localPosition, chunkCoordinates, blockData);
		}

		static SubChunk* getSubChunk(Pool<SubChunk, World::ChunkCapacity * 16>* subChunks, SubChunk* currentSubChunk, int currentLevel, const glm::ivec2& chunkCoordinates)
		{
			bool needsNewChunk = currentSubChunk == nullptr
				|| currentSubChunk->subChunkLevel != currentLevel
				|| currentSubChunk->numVertsUsed + 6 >= World::MaxVertsPerSubChunk;

			if (needsNewChunk && currentSubChunk)
			{
				currentSubChunk->state = SubChunkState::UploadVerticesToGpu;
			}

			SubChunk* ret = currentSubChunk;
			if (needsNewChunk)
			{
				ret = subChunks->getNewPool();
				ret->state = SubChunkState::TesselatingVertices;
				ret->subChunkLevel = currentLevel;
				ret->chunkCoordinates = chunkCoordinates;
			}
			return ret;
		}

		void generateRenderData(Pool<SubChunk, World::ChunkCapacity * 16>* subChunks, const Block* blockData, const glm::ivec2& chunkCoordinates)
		{
			const int worldChunkX = chunkCoordinates.x * 16;
			const int worldChunkZ = chunkCoordinates.y * 16;

			SubChunk* currentSubChunk = nullptr;
			for (int y = 0; y < World::ChunkHeight; y++)
			{
				int currentLevel = y / 16;

				for (int x = 0; x < World::ChunkDepth; x++)
				{
					for (int z = 0; z < World::ChunkWidth; z++)
					{
						// 24 Vertices per cube
						const Block& block = getBlockInternal(blockData, x, y, z, chunkCoordinates);
						int blockId = block.id;

						if (block == BlockMap::NULL_BLOCK)
						{
							continue;
						}

						const BlockFormat& blockFormat = BlockMap::getBlock(blockId);
						const TextureFormat& side = BlockMap::getTextureFormat(blockFormat.sideTexture);
						const TextureFormat& top = BlockMap::getTextureFormat(blockFormat.topTexture);
						const TextureFormat& bottom = BlockMap::getTextureFormat(blockFormat.bottomTexture);

						glm::ivec3 verts[8];
						verts[0] = glm::ivec3(
							x,
							y,
							z
						);
						verts[1] = verts[0] + glm::ivec3(0, 0, 1);
						verts[2] = verts[1] + glm::ivec3(1, 0, 0);
						verts[3] = verts[0] + glm::ivec3(1, 0, 0);

						verts[4] = verts[0] + glm::ivec3(0, 1, 0);
						verts[5] = verts[1] + glm::ivec3(0, 1, 0);
						verts[6] = verts[2] + glm::ivec3(0, 1, 0);
						verts[7] = verts[3] + glm::ivec3(0, 1, 0);

						// Top Face
						const int topBlockId = getBlockInternal(blockData, x, y + 1, z, chunkCoordinates).id;
						const BlockFormat& topBlock = BlockMap::getBlock(topBlockId);
						if (!topBlockId || topBlock.isTransparent)
						{
							currentSubChunk = getSubChunk(subChunks, currentSubChunk, currentLevel, chunkCoordinates);
							loadBlock(currentSubChunk->data + currentSubChunk->numVertsUsed, verts[5], verts[6], verts[7], verts[4],
								top, CUBE_FACE::TOP);
							currentSubChunk->numVertsUsed += 6;
						}

						// Bottom Face
						const int bottomBlockId = getBlockInternal(blockData, x, y - 1, z, chunkCoordinates).id;
						const BlockFormat& bottomBlock = BlockMap::getBlock(bottomBlockId);
						if (!bottomBlockId || bottomBlock.isTransparent)
						{
							currentSubChunk = getSubChunk(subChunks, currentSubChunk, currentLevel, chunkCoordinates);
							loadBlock(currentSubChunk->data + currentSubChunk->numVertsUsed, verts[0], verts[3], verts[2], verts[1],
								bottom, CUBE_FACE::BOTTOM);
							currentSubChunk->numVertsUsed += 6;
						}

						// Right Face
						const int rightBlockId = getBlockInternal(blockData, x, y, z + 1, chunkCoordinates).id;
						const BlockFormat& rightBlock = BlockMap::getBlock(rightBlockId);
						if (!rightBlockId || rightBlock.isTransparent)
						{
							currentSubChunk = getSubChunk(subChunks, currentSubChunk, currentLevel, chunkCoordinates);
							loadBlock(currentSubChunk->data + currentSubChunk->numVertsUsed, verts[2], verts[6], verts[5], verts[1],
								side, CUBE_FACE::RIGHT);
							currentSubChunk->numVertsUsed += 6;
						}

						// Left Face
						const int leftBlockId = getBlockInternal(blockData, x, y, z - 1, chunkCoordinates).id;
						const BlockFormat& leftBlock = BlockMap::getBlock(leftBlockId);
						if (!leftBlockId || leftBlock.isTransparent)
						{
							currentSubChunk = getSubChunk(subChunks, currentSubChunk, currentLevel, chunkCoordinates);
							loadBlock(currentSubChunk->data + currentSubChunk->numVertsUsed, verts[0], verts[4], verts[7], verts[3],
								side, CUBE_FACE::LEFT);
							currentSubChunk->numVertsUsed += 6;
						}

						// Forward Face
						const int forwardBlockId = getBlockInternal(blockData, x + 1, y, z, chunkCoordinates).id;
						const BlockFormat& forwardBlock = BlockMap::getBlock(forwardBlockId);
						if (!forwardBlockId || forwardBlock.isTransparent)
						{
							currentSubChunk = getSubChunk(subChunks, currentSubChunk, currentLevel, chunkCoordinates);
							loadBlock(currentSubChunk->data + currentSubChunk->numVertsUsed, verts[7], verts[6], verts[2], verts[3],
								side, CUBE_FACE::FRONT);
							currentSubChunk->numVertsUsed += 6;
						}

						// Back Face
						const int backBlockId = getBlockInternal(blockData, x - 1, y, z, chunkCoordinates).id;
						const BlockFormat& backBlock = BlockMap::getBlock(backBlockId);
						if (!backBlockId || backBlock.isTransparent)
						{
							currentSubChunk = getSubChunk(subChunks, currentSubChunk, currentLevel, chunkCoordinates);
							loadBlock(currentSubChunk->data + currentSubChunk->numVertsUsed, verts[0], verts[1], verts[5], verts[4],
								side, CUBE_FACE::BACK);
							currentSubChunk->numVertsUsed += 6;
						}
					}
				}
			}

			if (currentSubChunk && currentSubChunk->numVertsUsed > 0)
			{
				currentSubChunk->state = SubChunkState::UploadVerticesToGpu;
			}

			//for (int i = 0; i < (*subChunks).size(); i++)
			//{
			//	if ((*subChunks)[i]->chunkCoordinates == chunkCoordinates && (*subChunks)[i]->state == SubChunkState::RetesselateVertices)
			//	{
			//		g_logger_info("UNLOADING RETESSELATED STUFF");
			//		(*subChunks)[i]->state = SubChunkState::Unloaded;
			//	}
			//}
		}

		void serialize(const std::string& worldSavePath, const Block* blockData, const glm::ivec2& chunkCoordinates)
		{
			std::string filepath = getFormattedFilepath(chunkCoordinates, worldSavePath);
			FILE* fp = fopen(filepath.c_str(), "wb");
			fwrite(blockData, sizeof(Block) * World::ChunkWidth * World::ChunkHeight * World::ChunkDepth, 1, fp);
			fclose(fp);
		}

		void deserialize(Block* blockData, const std::string& worldSavePath, const glm::ivec2& chunkCoordinates)
		{
			std::string filepath = getFormattedFilepath(chunkCoordinates, worldSavePath);
			FILE* fp = fopen(filepath.c_str(), "rb");
			if (!fp)
			{
				g_logger_error("Could not open file '%s'", filepath.c_str());
				return;
			}

			fread(blockData, sizeof(Block) * World::ChunkWidth * World::ChunkHeight * World::ChunkDepth, 1, fp);
			fclose(fp);
		}

		bool exists(const std::string& worldSavePath, const glm::ivec2& chunkCoordinates)
		{
			std::string filepath = getFormattedFilepath(chunkCoordinates, worldSavePath);
			return File::isFile(filepath.c_str());
		}

		// =====================================================
		// Internal functions 
		// =====================================================
		static int to1DArray(int x, int y, int z)
		{
			return (x * World::ChunkDepth) + (y * World::ChunkHeight) + z;
		}

		static Block getBlockInternal(const Block* data, int x, int y, int z, const glm::ivec2& chunkCoordinates)
		{
			int index = to1DArray(x, y, z);
			return x >= 16 || x < 0 || z >= 16 || z < 0
				? ChunkManager::getBlock(glm::vec3(chunkCoordinates.x * 16.0f + x, y, chunkCoordinates.y * 16.0f + z))
				: y >= 256 || y < 0
				? BlockMap::NULL_BLOCK
				: data[index];
		}

		static std::string getFormattedFilepath(const glm::ivec2& chunkCoordinates, const std::string& worldSavePath)
		{
			return worldSavePath + "/" + std::to_string(chunkCoordinates.x) + "_" + std::to_string(chunkCoordinates.y) + ".bin";
		}

		static int toCompressedVec3(int x, int y, int z)
		{
			return (x * BASE_17_DEPTH) + (y * BASE_17_HEIGHT) + z;
		}

		static glm::ivec3 toCoordinates(int index)
		{
			const int z = index % BASE_17_WIDTH;
			const int x = (index % BASE_17_HEIGHT) / BASE_17_DEPTH;
			const int y = (index - (x * BASE_17_DEPTH) - z) / BASE_17_HEIGHT;
			return {
				x, y, z
			};
		}

		static Vertex compress(const glm::ivec3& vertex, const TextureFormat& texture, CUBE_FACE face, UV_INDEX uvIndex)
		{
			// Bits  0-16 position index
			// Bits 17-28 texId
			// Bits 29-31 normalDir face value
			uint32 data1 = 0;

			int positionIndex = toCompressedVec3(vertex.x, vertex.y, vertex.z);
			data1 |= ((positionIndex << 0) & POSITION_INDEX_BITMASK);
			data1 |= ((texture.id << 17) & TEX_ID_BITMASK);
			data1 |= ((uint32)face << 29) & FACE_BITMASK;

			uint32 data2 = 0;

			data2 |= (((uint32)uvIndex << 0) & UV_INDEX_BITMASK);

			return {
				data1,
				data2
			};
		}

		static glm::ivec3 extractPosition(uint32 data)
		{
			int positionIndex = data & POSITION_INDEX_BITMASK;
			return toCoordinates(positionIndex);
		}

		static uint16 extractTexId(uint32 data)
		{
			return (data & TEX_ID_BITMASK) >> 17;
		}

		static CUBE_FACE extractFace(uint32 data)
		{
			return (CUBE_FACE)((data & FACE_BITMASK) >> 29);
		}

		static UV_INDEX extractCorner(uint32 data)
		{
			return (UV_INDEX)((data & UV_INDEX_BITMASK) >> 0);
		}

		static void loadBlock(
			Vertex* vertexData,
			const glm::ivec3& vert1,
			const glm::ivec3& vert2,
			const glm::ivec3& vert3,
			const glm::ivec3& vert4,
			const TextureFormat& texture,
			CUBE_FACE face)
		{
			vertexData[0] = compress(vert1, texture, face, UV_INDEX::BOTTOM_RIGHT);
			vertexData[1] = compress(vert2, texture, face, UV_INDEX::TOP_RIGHT);
			vertexData[2] = compress(vert3, texture, face, UV_INDEX::TOP_LEFT);

			vertexData[3] = compress(vert1, texture, face, UV_INDEX::BOTTOM_RIGHT);
			vertexData[4] = compress(vert3, texture, face, UV_INDEX::TOP_LEFT);
			vertexData[5] = compress(vert4, texture, face, UV_INDEX::BOTTOM_LEFT);

#ifdef _DEBUG
			// TODO: Remove this once you are confident you are getting the right values
			g_logger_assert(extractPosition(vertexData[0].data1) == vert1, "Failed Position.");
			g_logger_assert(extractPosition(vertexData[1].data1) == vert2, "Failed Position.");
			g_logger_assert(extractPosition(vertexData[2].data1) == vert3, "Failed Position.");

			g_logger_assert(extractPosition(vertexData[3].data1) == vert1, "Failed Position.");
			g_logger_assert(extractPosition(vertexData[4].data1) == vert3, "Failed Position.");
			g_logger_assert(extractPosition(vertexData[5].data1) == vert4, "Failed Position.");

			g_logger_assert(extractFace(vertexData[0].data1) == face, "Failed Face");
			g_logger_assert(extractFace(vertexData[1].data1) == face, "Failed Face");
			g_logger_assert(extractFace(vertexData[2].data1) == face, "Failed Face");
			g_logger_assert(extractFace(vertexData[3].data1) == face, "Failed Face");

			g_logger_assert(extractTexId(vertexData[0].data1) == texture.id, "Failed Texture Id");
			g_logger_assert(extractTexId(vertexData[1].data1) == texture.id, "Failed Texture Id");
			g_logger_assert(extractTexId(vertexData[2].data1) == texture.id, "Failed Texture Id");
			g_logger_assert(extractTexId(vertexData[3].data1) == texture.id, "Failed Texture Id");

			g_logger_assert(extractCorner(vertexData[0].data2) == UV_INDEX::BOTTOM_RIGHT, "Failed on uv index");
			g_logger_assert(extractCorner(vertexData[1].data2) == UV_INDEX::TOP_RIGHT, "Failed on uv index");
			g_logger_assert(extractCorner(vertexData[2].data2) == UV_INDEX::TOP_LEFT, "Failed on uv index");
			g_logger_assert(extractCorner(vertexData[3].data2) == UV_INDEX::BOTTOM_RIGHT, "Failed on uv index");
			g_logger_assert(extractCorner(vertexData[4].data2) == UV_INDEX::TOP_LEFT, "Failed on uv index");
			g_logger_assert(extractCorner(vertexData[5].data2) == UV_INDEX::BOTTOM_LEFT, "Failed on uv index");
#endif
		}
	}
}