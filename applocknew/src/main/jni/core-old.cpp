#include <unistd.h>
#include <iostream>
#include <fstream>
#include "sha256.h"
#include <errno.h>
#include <jni.h>

#define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0]))) 

std::string targetPath;

static const char* sha256c(JNIEnv* env, jstring fileName)
{
    const char* f = env->GetStringUTFChars(fileName, 0);
    std::string r = sha256(std::string(f));
    env->ReleaseStringUTFChars(fileName, f);
    char* tar = const_cast<char*>(r.substr(6, -1).c_str());
    for(int i=0; i<4; ++i)
        tar[i] = '0';
    return tar;
}

static jstring sha256j(JNIEnv* env, jclass clazz, jstring fileName)
{
    return env->NewStringUTF(sha256c(env, fileName));
}

static jboolean encrypt(JNIEnv* env, jclass clazz, jstring fileName)
{
    const char* target = sha256c(env, fileName);
    const char* origin = env->GetStringUTFChars(fileName, 0);
    std::string tmpPath(targetPath);
    tmpPath += target;
    
    using namespace std;
    fstream f(origin, ios::in | ios::out | ios::binary);
    bool good = f.good();
    streamoff length = f.tellg();
    f.seekg(0, ios::beg);
    size_t size = 40960;
    char buffer[40960];
    f.read(buffer, size);
    size = f.gcount();
    for(int i=0; i<size; ++i)
    {
        buffer[i] += 128 + i;
    }
    f.seekp(0, ios::beg);
    f.write(buffer, size);
    f.close();
    /*
    FILE* f = fopen(origin, "rb+");
    int ee = errno;
    if (f == 0) return false;
    
    char buffer[40960];
    ee = fseeko(f, 0, SEEK_END);
    ee = errno;
    off_t length = ftello(f);
    ee = errno;
    size_t size = 0;
    if (length > 40960) size = 40960; else size = (size_t)length;
    fseeko(f, 0, SEEK_SET);
    size = fread(&buffer, 1, size, f);
    ee = errno;
    for(int i=0; i<size; ++i)
    {
        buffer[i] += 128 + i;
    }
    ee = fseeko(f, 0, SEEK_SET);
    ee = errno;
    ee = fwrite(&buffer, 1, size, f);
    ee = errno;
    /*
    if (length > 2 * 40960)
    {
        fseeko(f, length - 40960, SEEK_SET);
        size = fread(&buffer, 1, 40960, f);
        for(int i=0; i<size; ++i)
        {
            buffer[i] += 128 + i;
        }
        fseeko(f, length - 40960, SEEK_SET);
        fwrite(&buffer, 1, size, f);
    }*/
    
    //fclose(f);
    
    //FILE* t = fopen(tmpPath.c_str(), "w+");
    int e = rename(origin, tmpPath.c_str());
    
    //int e = link(origin, tmpPath.c_str());
    int er = errno;
    env->ReleaseStringUTFChars(fileName, origin);
    return e == 0;
}

static jboolean decrypt(JNIEnv* env, jclass clazz, jstring fileName)
{
    const char* target = sha256c(env, fileName);
    const char* origin = env->GetStringUTFChars(fileName, 0);
    std::string tmpPath(targetPath);
    tmpPath += target;
    
    using namespace std;
    fstream f(tmpPath.c_str(), ios::in | ios::out | ios::binary);
    bool good = f.good();
    size_t size = 40960;
    char buffer[40960];
    f.read(buffer, size);
    size = f.gcount();
    for(int i=0; i<size; ++i)
    {
        buffer[i] -= 128 + i;
    }
    f.seekp(0, ios::beg);
    f.write(buffer, size);
    f.close();
    
    /*
    FILE* f = fopen(tmpPath.c_str(), "rb+");
    if (f == 0) return false;
    
    char buffer[40960];
    fseeko(f, 0, SEEK_END);
    off_t length = ftello(f);
    size_t size = 0;
    if (size > 40960) size = 40960; else size = (size_t)length;
    fseeko(f, 0, SEEK_SET);
    size = fread(&buffer, 1, size, f);
    for(int i=0; i<size; ++i)
    {
        buffer[i] -= 128 + i;
    }
    fseeko(f, 0, SEEK_SET);
    fwrite(&buffer, 1, size, f);
    /*
    if (length > 2 * 40960)
    {
        fseeko(f, length - 40960, SEEK_SET);
        size = fread(&buffer, 1, 40960, f);
        for(int i=0; i<size; ++i)
        {
            buffer[i] -= 128 + i;
        }
        fseeko(f, length - 40960, SEEK_SET);
        fwrite(&buffer, 1, size, f);
    }
    */
    //fclose(f);
    
    int e = rename(tmpPath.c_str(), origin);
    env->ReleaseStringUTFChars(fileName, origin);
    
    return e == 0;
}

static void checkHeader(JNIEnv* env, jclass clazz, jstring fileName)
{
    const char* target = sha256c(env, fileName);
    std::string tmpPath(targetPath);
    tmpPath += target;
    
    using namespace std;
    fstream f(tmpPath.c_str(), ios::in | ios::binary);
    bool good = f.good();
    size_t size = 40960;
    char buffer[40960];
    f.read(buffer, size);
    size = f.gcount();
    f.close();
    
    tmpPath += ".txt";
    ofstream o(tmpPath.c_str(), ios::out | ios::binary);
    o.write(buffer, size);
    o.close();
}

static void init(JNIEnv* env, jclass clazz, jstring path)
{
    const char* p = env->GetStringUTFChars(path, 0);
    targetPath += p;
    targetPath += "/.superlock/";
    env->ReleaseStringUTFChars(path, p);
}

static jstring path(JNIEnv* env, jclass clazz)
{
    return env->NewStringUTF(targetPath.c_str());
}

static const JNINativeMethod methods_table[] = 
{
    "e", "(Ljava/lang/String;)Z", (void*)encrypt,
    "d", "(Ljava/lang/String;)Z", (void*)decrypt,
    "s", "(Ljava/lang/String;)Ljava/lang/String;", (void*)sha256j,
    "i", "(Ljava/lang/String;)V", (void*)init,
    "p", "()Ljava/lang/String;", (void*)path,
    "ch", "(Ljava/lang/String;)V", (void*)checkHeader
};

extern "C"
{
	jint JNI_OnLoad(JavaVM* vm, void* reserved)
	{
		JNIEnv* env = NULL;
		jint result = -1;
		
		if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
			return result;
		}

		env->RegisterNatives(env->FindClass("com/huale/applock/AppCore"), methods_table, NELEM(methods_table));
		
		return JNI_VERSION_1_4;
	}
}