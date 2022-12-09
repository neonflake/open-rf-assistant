# Open RF Assistant

## Purpose
The purpose of this project is to showcase the Java source code of a software solution, with which R&S RF profiles
can be maintained. This source code is intended for educational purposes only! It is not recommended to use a compiled
version without proper knowledge of R&S RF testing methods. The program can only function properly if, the correct RF test
software data is provided, the correct image resources are allocated, the intended RF scripts are used for testing! These
are not part of the provided source code. Use it at your own risk!

## Function
Software assisted maintenance of R&S CMU and CMW profiles, with the help of analysing and displaying the previous
measurements (test reports). It can create, manually edit and modify profiles with the help of a point-graph distortion.
It also provides profile synchronisation, logging and backup services. The software can also assist the RF testing
process by listing the daily test reports, allowing search between them and providing equipment information. It also
has a separate updater software, which can help with regular updates. The software works as a traditional desktop
application by providing a graphical user interface.

## Structure
This project contains three modules:
- RfAssistant: RF profile maintenance software
- UpdateAssistant: Updater software
- Commons: common graphical, data access and utility code used by all applications

## Requirements
- Java JDK 11, org.json JSON processing libraries