# Git-Like
## Overview
Git-Like is a simplified version of Git implemented in Java. It provides basic version control functionalities such as initializing a repository, creating commits, and managing objects. It now also includes user authentication to enhance system security.


## Features
- Initialize Repository: Create a new repository with the necessary directory structure.
- Create Commits: Record changes to the repository with commit messages.
- Manage Objects: Handle blob and tree objects for storing file contents and directory structures.
- User Authentication: Before executing any command, the user must authenticate.
Register new users.
Log in with username and password.
Log out.

## Usage
Authentication
Before executing any command, the user must authenticate. If the user is not registered, they will be prompted to create an account.


1.Register a User
To register a new user, run the program for the first time and choose the registration option:
```bash
java -cp target/classes com.david.study.Main
```
1. Register user
2. Log in
   
Select an option:

After entering a username and password, the program will close, and you will need to run it again to log in.

Log In
Once a user is registered, on the next run of the program, you can log in:
```bash
java -cp target/classes com.david.study.Main
```
If authentication is successful, you can execute system commands.
If authentication is successful, you can execute system commands.

Log Out
To log out, execute the following command:
```bash
java -cp target/classes com.david.study.Main logout
```
This will delete the saved session and prompt for authentication on the next execution.

Main Commands


2. Initialize Repository
   To initialize a new repository, run the following command:
```bash
  java -cp target/classes com.david.study.Main init
```

3. Create a Commit
   To create a commit, ensure you have made changes to the files and then run:
```bash
  java -cp target/classes com.david.study.Main commit -m "Your commit message"
```

4. Check log
   To show commits log, run:
```bash
  java -cp target/classes com.david.study.Main log
```

5. Switch between commits
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
