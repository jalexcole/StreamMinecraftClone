// Implementations
// TODO: Remove all includes in headers
// TODO: Only allowed to include <stdint> and <stdbool> in headers
#define GABE_CPP_UTILS_IMPL
#include <CppUtils/CppUtils.h>
#undef GABE_CPP_UTILS_IMPL
#define STB_IMAGE_IMPLEMENTATION
#include <stb/stb_image.h>
#undef STB_IMAGE_IMPLEMENTATION
#define STB_IMAGE_WRITE_IMPLEMENTATION
#include <stb/stb_write.h>
#undef STB_IMAGE_WRITE_IMPLEMENTATION

#include <CppUtils/CppUtils.h>
#include "core/Application.h"

int main()
{
	CppUtils::Memory::init();

	Minecraft::Application::run();

	return 0;
}
