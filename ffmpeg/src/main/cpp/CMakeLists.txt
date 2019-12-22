cmake_minimum_required(VERSION 3.4.1)

AUX_SOURCE_DIRECTORY(. DIRSRCS)

#C 的编译选项是 CMAKE_C_FLAGS
# 指定编译参数，可选
#SET(CMAKE_CXX_FLAGS "-Wno-error=format-security -Wno-error=pointer-sign")
#SET(CMAKE_CXX_FLAGS "-Wdeprecated-declarations -Wunused-command-line-argument")

add_library(
        ffmpeg-lib

        SHARED
        ${DIRSRCS})
find_library(
        log-lib
        log)
#set(JNI_LIBS_DIR ${CMAKE_SOURCE_DIR}/../../../libs)
#添加其他预编译库方式1（推荐使用）
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_SOURCE_DIR}/../../../libs/${ANDROID_ABI}")


#添加其他与编译库方式2（练习时可以尝试）
#add_library(avutil
#        SHARED
#        IMPORTED )
#set_target_properties(avutil
#        PROPERTIES IMPORTED_LOCATION
#        ${JNI_LIBS_DIR}/${CMAKE_ANDROID_ARCH_ABI}/libavutil.so )
#
#add_library(swresample
#        SHARED
#        IMPORTED )
#set_target_properties(swresample
#        PROPERTIES IMPORTED_LOCATION
#        ${JNI_LIBS_DIR}/${CMAKE_ANDROID_ARCH_ABI}/libswresample.so )
#
#add_library(swscale
#        SHARED
#        IMPORTED )
#set_target_properties(swscale
#        PROPERTIES IMPORTED_LOCATION
#        ${JNI_LIBS_DIR}/${CMAKE_ANDROID_ARCH_ABI}/libswscale.so )
#
#add_library(avcodec
#        SHARED
#        IMPORTED )
#set_target_properties(avcodec
#        PROPERTIES IMPORTED_LOCATION
#        ${JNI_LIBS_DIR}/${CMAKE_ANDROID_ARCH_ABI}/libavcodec.so )
#
#add_library(avformat
#        SHARED
#        IMPORTED )
#set_target_properties(avformat
#        PROPERTIES IMPORTED_LOCATION
#        ${JNI_LIBS_DIR}/${CMAKE_ANDROID_ARCH_ABI}/libavformat.so )
#
#add_library(avfilter
#        SHARED
#        IMPORTED )
#set_target_properties(avfilter
#        PROPERTIES IMPORTED_LOCATION
#        ${JNI_LIBS_DIR}/${CMAKE_ANDROID_ARCH_ABI}/libavfilter.so )
#
#
#add_library(avdevice
#        SHARED
#        IMPORTED )
#set_target_properties(avdevice
#        PROPERTIES IMPORTED_LOCATION
#        ${JNI_LIBS_DIR}/${ANDROID_ABI}/libavdevice.so )

include_directories(include/)


target_link_libraries( # Specifies the target library.
        ffmpeg-lib
        avutil swresample swscale avcodec avformat avfilter avdevice
        log
        ${log-lib})