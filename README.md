# Git-Like
## Overview
Git-Like is a simplified version of Git implemented in Java. It provides basic version control functionalities such as initializing a repository, creating commits, and managing objects.


## Features
- Initialize Repository: Create a new repository with the necessary directory structure.
- Create Commits: Record changes to the repository with commit messages.
- Manage Objects: Handle blob and tree objects for storing file contents and directory structures.

## Usage
1. Initialize Repository
   To initialize a new repository, run the following command:
```bash
  java -cp target/classes com.david.study.Main init
```

2. Create a Commit
   To create a commit, ensure you have made changes to the files and then run:
```bash
  java -cp target/classes com.david.study.Main commit -m "Your commit message"
```

3. Check log
   To show commits log, run:
```bash
  java -cp target/classes com.david.study.Main log
```

4. Switch between commits
   To switch run:
```bash
  java -cp target/classes com.david.study.Main checkout <commit-hash>
```

## Directory Structure
The project follows a standard Maven directory structure:
```bash
  git-like/
  ├── .idea/
  ├── src/
  │   ├── main/
  │   │   ├── java/
  │   │   │   └── com/
  │   │   │       └── david/
  │   │   │           └── study/
  │   │   └── resources/
  │   └── test/
  │       └── java/
  └── target/
  ├── classes/
  │   └── com/
  │       └── david/
  │           └── study/
  └── generated-sources/
  └── annotations/
```

## License
This project is licensed under the MIT License.

Feel free to customize this README.md file further to suit your project's needs.
