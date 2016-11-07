::打包之后将progurad日志和jni的obj文件保存下来，便于DEBUG
cd src/main/obj/
set suffix=%time:~0,2%-%time:~3,2%-%time:~6,2%
"C:\Program Files\HaoZip\HaoZipC.exe" a ../../../pkg-local-%suffix%.zip *
cd ../../../build/outputs/mapping/release/
"C:\Program Files\HaoZip\HaoZipC.exe" a ../../../../pkg-mapping-%suffix%.zip *
::"C:\Program Files\HaoZip\HaoZipC.exe" a pkg-log-%time:~0,2%-%time:~3,2%-%time:~6,2%.zip src/main/obj/* build/outputs/mapping/release/*