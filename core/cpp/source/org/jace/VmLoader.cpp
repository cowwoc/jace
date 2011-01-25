
#include "org/jace/VmLoader.h"

org::jace::VmLoader::VmLoader(jint _jniVersion):
	jniVersion(_jniVersion)
{}

jint org::jace::VmLoader::getJniVersion() const
{
	return jniVersion;
}
