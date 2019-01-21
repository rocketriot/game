## Prerequisites

- Java JDK (1.8.x)
- Maven (https://maven.apache.org/install.html)
- git

## Getting started (without Eclipse)

1. Clone the repository
2. `mvn package` in the root folder
3. Run
```bash
mvn compile exec:java "-Dexec.mainClass=bham.bioshock.HelloWorld"
```

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

