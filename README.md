# Generating Projects

To generate a new OpenGL project open up a command prompt window in this directory, then simply type in:

```bat
build.bat create myProject
```

where `myProject` is the name of your project. Then run:

```bat
build.bat vs2019
```

You can use vs2015, vs2017, codelite, xcode, etc. To see all options just type in `build.bat` and hit enter. Once you have a generated project file click onto the project file then compile and run your project and you should be greeted with a window and a console window printing out 'Hello OpenGL'.

## Requirements

In order to use this build script you must have python and git installed. This means you should be able to run:

```bat
python --version
git --version
```

and not encounter any errors. If this works then you should be able to follow the instructions above and generate an OpenGL project.
