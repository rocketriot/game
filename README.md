## Prerequisites

- Java JDK (1.8.x)
- Gradle
- git

## Getting started (with Eclipse or Intelij)

1. Clone the repository
2. Import as a gradle project (Eclipse) or import the build.gradle file (Intelij)
3. To start the program run DesktopLauncher.java in the desktop package
4. This will throw an error as it won't be able to find the assets
5. Change the working directory to core/assets by editing the run configuration 

## Contributing

### 1. To create a new feature:
 - create a separate branch  
 eg:
 ```bash
  git checkout -b networking
 ```
 - make commits to that branch
 ```bash
  git add .
  git commit -m "message"
  git push
 ```
 - if the feature is ready, make a `Merge Request` to the master

### 2. To contribute to the feature that is currently being developed:
 - create a new branch (with the name of the feature and your name)  
 eg:
 ```bash
  git checkout -b networking-myname
 ```
 - make changes & commit them
 - Make a `Merge Request` **To the main branch for that feature**
 - If the feature is ready, make a `Merge Request` to the master

