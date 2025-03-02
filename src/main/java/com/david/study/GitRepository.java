package com.david.study;

import com.david.study.impl.GitObjectStore;
import com.david.study.impl.GitRepositoryManager;
import com.david.study.impl.SHA1HashGenerator;
import com.david.study.interfaces.HashGenerator;
import com.david.study.interfaces.ObjectStore;
import com.david.study.interfaces.RepositoryManager;

import java.io.IOException;

public class GitRepository {
    private final RepositoryManager repositoryManager;

    public GitRepository() {
        HashGenerator hashGenerator = new SHA1HashGenerator();
        ObjectStore objectStore = new GitObjectStore(hashGenerator);
        this.repositoryManager = new GitRepositoryManager(objectStore, hashGenerator);
    }

    // Constructor con inyección de dependencias para facilitar pruebas
    public GitRepository(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    // Inicia el repositorio Git-like
    public void init() throws IOException {
        repositoryManager.init();
    }

    // Crea un commit integrando el estado del árbol y actualiza la referencia HEAD
    public String commitTree(String message) throws IOException {
        return repositoryManager.commitTree(message);
    }

    // Muestra el log de commits recorriendo el historial
    public void showCommitLog() throws IOException {
        repositoryManager.showCommitLog();
    }

    // Realiza el checkout de un commit específico reconstruyendo el árbol de archivos
    public void checkout(String commitHash) throws IOException {
        repositoryManager.checkout(commitHash);
    }
}
