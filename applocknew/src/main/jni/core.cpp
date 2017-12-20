#ifdef _64BIT_SUPPORT_HL
#define _LARGE_FILES
#define _FILE_OFFSET_BITS 64
#define _OFF_T_DEFINED_
typedef long long off_t;
#endif
#include <unistd.h>
#include <sys/stat.h>
#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <string>
#include <jni.h>
#include<android/log.h>
#include <sys/ptrace.h>
#include "sha256.h"

#ifdef DEBUG
#define LOGE(...) __android_log_print(ANDROID_LOG_FATAL, "jni", __VA_ARGS__)
#else
#define LOGE(...)
#endif
#define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))

#ifndef nullptr
#define nullptr NULL
#endif

#define ERROR_NO_ERROR 0
#define ERROR_NOT_FOUND 1
#define ERROR_RENAME_FAILS 2
#define ERROR_BACKUP_FAILS 3
#define ERROR_SAVE_INDEX_FAILS 4
#define ERROR_READ_BACKUP_FAILS 5
#define ERROR_WRITE_FAILS 6

std::string rootPath;
std::string thumbPath;
std::string dataPath;
std::string backupPath;
std::string infoPath;
jint errorCode;

#define IF_FILENAME_INVALID_THEN_RETURN errorCode = ERROR_NO_ERROR; \
if (fileName == NULL) return

#define GET_UTF_CHARS(jstring) env->GetStringUTFChars(jstring, 0)
#define TO_UTF_CHARS(cstring) env->NewStringUTF(cstring)
#define DELETE_UTF_CHARS(jstring, cstring) env->ReleaseStringUTFChars(jstring, cstring)

#define SAVE_INDEX_FAILS_RETURN_FALSE errorCode = ERROR_SAVE_INDEX_FAILS;\
        return false

#define BACKUP_FILE_FAILS_RETURN_FALSE errorCode = ERROR_BACKUP_FAILS;\
        return false

#define READ_BACKUP_FILE_FAILS_RETURN_FALSE errorCode = ERROR_READ_BACKUP_FAILS;\
        return false

#define TARGET_FILE_NOT_FOUND_RETURN_FALSE errorCode = ERROR_NOT_FOUND;\
        return false

static jstring SHA256(JNIEnv* env, jclass clazz, jstring fileName)
{
    IF_FILENAME_INVALID_THEN_RETURN NULL;
    const char* cs = GET_UTF_CHARS(fileName);
    jstring jst = TO_UTF_CHARS(sha256(cs).c_str());
    DELETE_UTF_CHARS(fileName, cs);
    return jst;
}

static jstring SHA256WithPath(JNIEnv* env, jclass clazz, jstring fileName, jboolean thumb)
{
    IF_FILENAME_INVALID_THEN_RETURN NULL;
    const char* cs = GET_UTF_CHARS(fileName);
    jstring jst = TO_UTF_CHARS(((thumb ? thumbPath : dataPath) + sha256(cs)).c_str());
    DELETE_UTF_CHARS(fileName, cs);
    return jst;
}

static jstring GetFileIndex(JNIEnv* env, jclass clazz, jstring fileName)
{
    IF_FILENAME_INVALID_THEN_RETURN NULL;
    const char* origin = GET_UTF_CHARS(fileName);
    LOGE("well, origin is %s", origin);

    FILE* f = fopen(origin, "rb");
    if (f == 0)
    {
        errorCode = ERROR_NOT_FOUND;
        LOGE("error, open file %s failure, errno %i", origin, errno);
        DELETE_UTF_CHARS(fileName, origin);
        return NULL;
    }

    char buffer[1025];
    int size = fread(&buffer, 1, 1024, f);

    LOGE("read file %s for size %i, buffer %s", origin, size, buffer);

    for(int i=0; i<size; ++i)
    {
        buffer[i] -= 128 + i;
    }

	//因为字符串的读取会到0为止，而第一个字节又是文件类型，所以为0的文件类型设置为127，在java端再转回来
    buffer[size] = 0;
    if (buffer[0] == 0)
        buffer[0] = 127;

    fclose(f);
    DELETE_UTF_CHARS(fileName, origin);
    return TO_UTF_CHARS(buffer);
}

static bool SaveFileIndex(const char* target, char* buffer, int size)
{
    FILE* f = fopen(target, "wb+");
    if (f == 0) {
      SAVE_INDEX_FAILS_RETURN_FALSE;
    }

    for(int i=0; i<size; ++i)
    {
        buffer[i] += 128 + i;
    }
    int r = TEMP_FAILURE_RETRY(fwrite(buffer, 1, size, f));
    fclose(f);

    if (r == size) return true;

    SAVE_INDEX_FAILS_RETURN_FALSE;
}

static bool BackupFile(const char* target, const char* backup, char*& buffer, size_t& size)
{
  FILE* t = fopen(target, "rb");
  if (t == 0)
  {
    TARGET_FILE_NOT_FOUND_RETURN_FALSE;
  }
  LOGE("here now1....%i", 1);
  struct stat sb;
  int fd = fileno(t);
  int rc = TEMP_FAILURE_RETRY(fstat(fd, &sb));
  off_t length = sb.st_size;
  LOGE("length is %ld", length);
  if (length < 0) length = 2147483647;

  if (length > 40960)
      size = 40960;
  else
      size = (size_t)length;
  buffer = new char[size];
  if (buffer == nullptr)
  {
    TARGET_FILE_NOT_FOUND_RETURN_FALSE;
  }

  LOGE("here now2....%i", 2);
  int result = TEMP_FAILURE_RETRY(fread(buffer, 1, size, t));
  fclose(t);

LOGE("here now3....%i ", 3);
  if (result != size)
  {
    TARGET_FILE_NOT_FOUND_RETURN_FALSE;
  }

  FILE* bk = fopen(backup, "wb+");
  if (bk == 0)
  {
    BACKUP_FILE_FAILS_RETURN_FALSE;
  }

  result = TEMP_FAILURE_RETRY(fwrite(buffer, 1, size, bk));
  fclose(bk);
LOGE("here now4....%i", 4);
  if (result == size) return true;

  BACKUP_FILE_FAILS_RETURN_FALSE;
}

static bool ReadBackupedFile(const char* backup, char*& buffer, size_t& size)
{
  FILE* f = fopen(backup, "rb");
  if (f == 0)
  {
    LOGE("haha, here fails");
    READ_BACKUP_FILE_FAILS_RETURN_FALSE;
  }

  struct stat sb;
  int fd = fileno(f);
  int rc = TEMP_FAILURE_RETRY(fstat(fd, &sb));
  size = (size_t)(sb.st_size);
  buffer = new char[size];
  LOGE("well, buffer here %i", buffer == nullptr ? 1 : 0);
  if (buffer == nullptr)
  {
    READ_BACKUP_FILE_FAILS_RETURN_FALSE;
  }

  int readsize = TEMP_FAILURE_RETRY(fread(buffer, 1, size, f));
  fclose(f);

  LOGE("buffer read size %i, target size %i", readsize, size);
  if (readsize == size) return true;

  READ_BACKUP_FILE_FAILS_RETURN_FALSE;
}

static jint lastErrorCode() {
    int err = errorCode;
    errorCode = ERROR_NO_ERROR;
    return err;
}

static bool encode(const char* fileName, char*& buffer, size_t& size)
{
    FILE* f = fopen(fileName, "r+b");
    if (f == 0)
    {
        TARGET_FILE_NOT_FOUND_RETURN_FALSE;
    }

    for(int i=0; i<size; ++i)
    {
        buffer[i] += 128 + i;
    }

    LOGE("encode size is %i", size);

    if (TEMP_FAILURE_RETRY(fseek(f, 0, SEEK_SET)) != 0)
    {
        TARGET_FILE_NOT_FOUND_RETURN_FALSE;
    }
    //这里即使被中断，也无关紧要了，就是安全性差一些，可能并没有将加密过后的字节写入文件，但不至于损坏文件
    TEMP_FAILURE_RETRY(fwrite(buffer, 1, size, f));

    fclose(f);

    return true;
}

static bool decode(const char* fileName, char*& buffer, size_t& size)
{
    FILE* f = fopen(fileName, "r+b");
    if (f == 0)
    {
        TARGET_FILE_NOT_FOUND_RETURN_FALSE;
    }

    if (TEMP_FAILURE_RETRY(fseek(f, 0, SEEK_SET)) != 0)
    {
        TARGET_FILE_NOT_FOUND_RETURN_FALSE;
    }

    int result = TEMP_FAILURE_RETRY(fwrite(buffer, 1, size, f));
    fclose(f);

    LOGE("result is %i and size %i", result, size);
    if (result == size) return true;

    errorCode = ERROR_WRITE_FAILS;
    return false;
}

static jboolean encrypt(JNIEnv* env, jclass clazz, jstring fileName, jbyte type)
{
    IF_FILENAME_INVALID_THEN_RETURN false;
    //int len = 0; //env->GetStringLength(fileName);
    const char* origin = GET_UTF_CHARS(fileName);
    std::string target = sha256(origin);
    std::string tmpPath = dataPath + target;
    std::string bkPath = backupPath + target;
    std::string ipath = infoPath + target;

    //write file info first
    char buf[1024];
    int len = sprintf(buf, "%c%s", type, origin);
    buf[len] = 0;

    if (!SaveFileIndex(ipath.c_str(), buf, len))
    {
        errorCode = ERROR_SAVE_INDEX_FAILS;
        DELETE_UTF_CHARS(fileName, origin);
        return false;
    }

    char* buffer = nullptr;
    size_t size = 0;
    //backup first
    if (!BackupFile(origin, bkPath.c_str(), buffer, size))
    {
      DELETE_UTF_CHARS(fileName, origin);
      if (buffer != nullptr) delete[] buffer;
      return false;
    }

    //move file to the data path
    if (rename(origin, tmpPath.c_str()) != 0)
    {
        LOGE("well, rename is error %s, code is %i", origin, errno);
        remove(ipath.c_str());
        remove(bkPath.c_str());
        delete[] buffer;
        DELETE_UTF_CHARS(fileName, origin);
        errorCode = ERROR_RENAME_FAILS;
        return false;
    }

    //operate the file
    encode(tmpPath.c_str(), buffer, size);
    delete[] buffer;

    DELETE_UTF_CHARS(fileName, origin);
    return true;
}


static bool DecryptFile(const char* origin, std::string& target)
{
  std::string tmpPath = dataPath + target;
  std::string bkPath = backupPath + target;
  std::string ipath = infoPath + target;

  char* buffer = nullptr;
  size_t size = 0;
  ReadBackupedFile(bkPath.c_str(), buffer, size);

  if (nullptr == buffer)
  {
    return false;
  }

  bool success = decode(tmpPath.c_str(), buffer, size);
  delete[] buffer;

  LOGE("success ? %i", success ? 1: 0);
  if (!success)
  {
    return false;
  }

  int e = rename(tmpPath.c_str(), origin);
  if (e != 0)
  {
      LOGE("rename fails tmp %s, target path is %s", tmpPath.c_str(), origin);
    errorCode = ERROR_RENAME_FAILS;
    return false;
  }

  remove(bkPath.c_str());
  remove(ipath.c_str());

LOGE("success ...");
  return true;
}

static jboolean decrypt(JNIEnv* env, jclass clazz, jstring fileName)
{
    IF_FILENAME_INVALID_THEN_RETURN false;
    const char* origin = GET_UTF_CHARS(fileName);
    std::string target = sha256(origin);

    bool result = DecryptFile(origin, target);
    DELETE_UTF_CHARS(fileName, origin);

    return result;
}

static jboolean DecryptLostFile(JNIEnv* env, jclass clazz, jstring filePath, jstring fileName)
{
    IF_FILENAME_INVALID_THEN_RETURN false;
    const char* origin = GET_UTF_CHARS(filePath);
    const char* filesha256 = GET_UTF_CHARS(fileName);
    std::string target = filesha256;

    bool result = DecryptFile(origin, target);

    DELETE_UTF_CHARS(filePath, origin);
    DELETE_UTF_CHARS(fileName, filesha256);

    return result;
}

static jstring PreviewFile(JNIEnv* env, jclass clazz, jstring fileName, jboolean start)
{
    LOGE("haha, wa %i", 1);
    IF_FILENAME_INVALID_THEN_RETURN NULL;
    const char* origin = GET_UTF_CHARS(fileName);
    std::string target = sha256(origin);
    std::string tmpPath = dataPath + target;
    std::string bkPath = backupPath + target;
    LOGE("haha, here %i", 2);

    char* buffer = nullptr;
    size_t size = 0;

    ReadBackupedFile(bkPath.c_str(), buffer, size);
    LOGE("haha, here read backup %i buffer %i", size, buffer == nullptr ? 1 : 0);

    if (buffer == nullptr) return NULL;

    bool result = false;
    if (start)
    {
      LOGE("haha, here start here");
        result = decode(tmpPath.c_str(), buffer, size);
    }
    else
    {
        result = encode(tmpPath.c_str(), buffer, size);
    }
    LOGE("haha, here delete %s", tmpPath.c_str());
    delete[] buffer;

    DELETE_UTF_CHARS(fileName, origin);

    return result ? TO_UTF_CHARS(tmpPath.c_str()) : NULL;
}

static void Init(JNIEnv* env, jclass clazz, jstring path)
{
    const char* p = GET_UTF_CHARS(path);
    rootPath = p;
    rootPath += "/";
    dataPath = rootPath + "d/";
    thumbPath = rootPath + "t/";
    backupPath = rootPath + "b/";
    infoPath = rootPath + "i/";
    DELETE_UTF_CHARS(path, p);
}

static jobjectArray GetPathArray(JNIEnv* env, jclass clazz)
{
   jobjectArray ret= (jobjectArray)env->NewObjectArray(4, env->FindClass("java/lang/String"), TO_UTF_CHARS(""));
   env->SetObjectArrayElement(ret, 0, TO_UTF_CHARS(dataPath.c_str()));
   env->SetObjectArrayElement(ret, 1, TO_UTF_CHARS(thumbPath.c_str()));
   env->SetObjectArrayElement(ret, 2, TO_UTF_CHARS(backupPath.c_str()));
   env->SetObjectArrayElement(ret, 3, TO_UTF_CHARS(infoPath.c_str()));
   return ret;
}

#define DATA_PATH 0
#define THUMB_PATH 1
#define INFO_PATH 2
#define BACKUP_PATH 3

static jstring GetPath(JNIEnv* env, jclass clazz, jint type)
{
    switch(type)
    {
        case DATA_PATH:
            return TO_UTF_CHARS(dataPath.c_str());

        case THUMB_PATH:
            return TO_UTF_CHARS(thumbPath.c_str());

        case INFO_PATH:
            return TO_UTF_CHARS(infoPath.c_str());

        case BACKUP_PATH:
            return TO_UTF_CHARS(backupPath.c_str());

        default:
            return NULL;
    }
}

static const JNINativeMethod methods_table[] =
{
    "en", "(Ljava/lang/String;B)Z", (void*)encrypt,
    "de", "(Ljava/lang/String;)Z", (void*)decrypt,
    "dd", "(Ljava/lang/String;Ljava/lang/String;)Z", (void*) DecryptLostFile,
    "sj", "(Ljava/lang/String;)Ljava/lang/String;", (void*)SHA256,
    "sj", "(Ljava/lang/String;Z)Ljava/lang/String;", (void*)SHA256WithPath,
    "i", "(Ljava/lang/String;)V", (void*)Init,
    "p", "()[Ljava/lang/String;", (void*)GetPathArray,
    "p", "(I)Ljava/lang/String;", (void*)GetPath,
    "pr", "(Ljava/lang/String;Z)Ljava/lang/String;", (void*)PreviewFile,
    "fi", "(Ljava/lang/String;)Ljava/lang/String;", (void*)GetFileIndex,
    "e", "()I", (void*)lastErrorCode,
};

extern "C"
{
	jint JNI_OnLoad(JavaVM* vm, void* reserved)
	{
        //ptrace(PTRACE_TRACEME, 0, 0, 0);
		JNIEnv* env = NULL;
        /*
        jint result = ptrace(PTRACE_TRACEME,0 ,0 ,0);
        if(result != 0){
            exit(0);
        }
        */

		if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
			return -1;
		}
		//MediaCore

		env->RegisterNatives(env->FindClass("com/vactorappsapi/manager/AppsCore"), methods_table, NELEM(methods_table));
		return JNI_VERSION_1_4;
	}
}
