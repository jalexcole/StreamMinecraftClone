#include "core.h"

#include "renderer/Framebuffer.h"
#include "renderer/Texture.h"

namespace Minecraft
{
	static void internalGenerate(Framebuffer& framebuffer);

	// =============================================================
	// Framebuffer
	// =============================================================
	void Framebuffer::bind() const
	{
		g_logger_assert(fbo != UINT32_MAX, "Tried to bind invalid framebuffer.");
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
	}

	void Framebuffer::unbind() const
	{
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	void Framebuffer::clearColorAttachmentUint32(int colorAttachment, uint32 clearColor) const
	{
		g_logger_assert(colorAttachment >= 0 && colorAttachment < colorAttachments.size(), "Index out of bounds. Color attachment does not exist '%d'.", colorAttachment);
		const Texture& texture = colorAttachments[colorAttachment];
		g_logger_assert(TextureUtil::byteFormatIsInt(texture), "Cannot clear non-uint texture as if it were a uint texture.");

		uint32 externalFormat = TextureUtil::toGlExternalFormat(texture.format);
		uint32 formatType = TextureUtil::toGlDataType(texture.format);
		glClearTexImage(texture.graphicsId, 0, externalFormat, formatType, &clearColor);
	}

	void Framebuffer::clearColorAttachmentRgb(int colorAttachment, glm::vec3 clearColor) const
	{
		g_logger_assert(colorAttachment >= 0 && colorAttachment < colorAttachments.size(), "Index out of bounds. Color attachment does not exist '%d'.", colorAttachment);
		const Texture& texture = colorAttachments[colorAttachment];
		g_logger_assert(TextureUtil::byteFormatIsRgb(texture), "Cannot clear non-rgb texture as if it were a rgb texture.");

		uint32 externalFormat = TextureUtil::toGlExternalFormat(texture.format);
		uint32 formatType = TextureUtil::toGlDataType(texture.format);
		glClearTexImage(texture.graphicsId, 0, externalFormat, formatType, &clearColor);
	}

	uint32 Framebuffer::readPixelUint32(int colorAttachment, int x, int y) const
	{
		g_logger_assert(colorAttachment >= 0 && colorAttachment < colorAttachments.size(), "Index out of bounds. Color attachment does not exist '%d'.", colorAttachment);
		const Texture& texture = colorAttachments[colorAttachment];
		g_logger_assert(TextureUtil::byteFormatIsInt(texture), "Cannot read non-uint texture as if it were a uint texture.");

		// If we are requesting an out of bounds pixel, return max uint32 which should be a good flag I guess
		// TODO: Create clearColor member variable in color attachments and return that instead here
		if (x < 0 || y < 0 || x >= colorAttachments[colorAttachment].width || y >= colorAttachments[colorAttachment].height)
		{
			return (uint32)-1;
		}

		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
		glReadBuffer(GL_COLOR_ATTACHMENT0 + colorAttachment);

		// 128 bits should be big enough for 1 pixel of any format
		// TODO: Come up with generic way to get any type of pixel data
		static uint32 pixel;
		uint32 externalFormat = TextureUtil::toGlExternalFormat(texture.format);
		uint32 formatType = TextureUtil::toGlDataType(texture.format);
		glReadPixels(x, y, 1, 1, externalFormat, formatType, &pixel);

		glBindFramebuffer(GL_FRAMEBUFFER, 0);

		return pixel;
	}

	const Texture& Framebuffer::getColorAttachment(int index) const
	{
		return colorAttachments.at(index);
	}

	void Framebuffer::regenerate()
	{
		g_logger_assert(fbo != UINT32_MAX, "Cannot regenerate framebuffer that has with Fbo id != UINT32_MAX.");
		if (includeDepthStencil)
		{
			g_logger_assert(rbo != UINT32_MAX, "Cannot regenerate framebuffer that has with Rbo id != UINT32_MAX.");
		}

		destroy(false);
		internalGenerate(*this);
	}

	void Framebuffer::destroy(bool clearColorAttachmentSpecs)
	{
		g_logger_assert(fbo != UINT32_MAX, "Tried to delete invalid framebuffer.");
		glDeleteFramebuffers(1, &fbo);
		fbo = UINT32_MAX;

		for (int i = 0; i < colorAttachments.size(); i++)
		{
			Texture& texture = colorAttachments[i];
			texture.destroy();
		}

		if (clearColorAttachmentSpecs)
		{
			colorAttachments.clear();
		}

		if (includeDepthStencil)
		{
			g_logger_assert(rbo != UINT32_MAX, "Tried to delete invalid renderbuffer.");
			glDeleteRenderbuffers(1, &rbo);
			rbo = UINT32_MAX;
		}
	}

	// =============================================================
	// Framebuffer Builder
	// =============================================================
	FramebufferBuilder::FramebufferBuilder(uint32 width, uint32 height)
	{
		framebuffer.fbo = UINT32_MAX;
		framebuffer.rbo = UINT32_MAX;
		framebuffer.width = width;
		framebuffer.height = height;
		framebuffer.includeDepthStencil = false;
		framebuffer.depthStencilFormat = ByteFormat::None;
	}

	Framebuffer FramebufferBuilder::generate()
	{
		internalGenerate(framebuffer);
		return framebuffer;
	}

	FramebufferBuilder& FramebufferBuilder::addColorAttachment(const Texture& textureSpecification)
	{
		framebuffer.colorAttachments.push_back(textureSpecification);
		return *this;
	}


	// Internal functions
	static void internalGenerate(Framebuffer& framebuffer)
	{
		g_logger_assert(framebuffer.fbo == UINT32_MAX, "Cannot generate framebuffer with Fbo already id == UINT32_MAX.");
		g_logger_assert(framebuffer.rbo == UINT32_MAX, "Cannot generate framebuffer with Rbo already id == UINT32_MAX.");
		g_logger_assert(framebuffer.colorAttachments.size() > 0, "Framebuffer must have at least 1 color attachment.");

		glGenFramebuffers(1, &framebuffer.fbo);
		glBindFramebuffer(GL_FRAMEBUFFER, framebuffer.fbo);

		if (framebuffer.colorAttachments.size() > 1)
		{
			g_logger_assert(framebuffer.colorAttachments.size() < 8, "Too many framebuffer attachments. Only 8 attachments supported.");
			static GLenum colorBufferAttachments[8] = { GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1, GL_COLOR_ATTACHMENT2, GL_COLOR_ATTACHMENT3, GL_COLOR_ATTACHMENT4, GL_COLOR_ATTACHMENT5, GL_COLOR_ATTACHMENT6, GL_COLOR_ATTACHMENT7 };
			glDrawBuffers(framebuffer.colorAttachments.size(), colorBufferAttachments);
		}

		// Create texture to render data to, and attach it to framebuffer
		for (int i = 0; i < framebuffer.colorAttachments.size(); i++)
		{
			Texture& texture = framebuffer.colorAttachments[i];
			texture.width = framebuffer.width;
			texture.height = framebuffer.height;
			TextureUtil::generateEmptyTexture(texture);
			glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + i, GL_TEXTURE_2D, texture.graphicsId, 0);
		}

		if (framebuffer.includeDepthStencil)
		{
			g_logger_assert(framebuffer.depthStencilFormat != ByteFormat::None, "Cannot add depth stencil information with no byte format.");

			// Create renderbuffer to store depth_stencil info
			glGenRenderbuffers(1, &framebuffer.rbo);
			glBindRenderbuffer(GL_RENDERBUFFER, framebuffer.rbo);
			uint32 glDepthFormat = TextureUtil::toGlExternalFormat(framebuffer.depthStencilFormat);
			glRenderbufferStorage(GL_RENDERBUFFER, glDepthFormat, framebuffer.width, framebuffer.height);
			glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, framebuffer.rbo);
		}

		if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
		{
			g_logger_assert(false, "Framebuffer is not complete.");
		}

		// Unbind framebuffer now
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
}