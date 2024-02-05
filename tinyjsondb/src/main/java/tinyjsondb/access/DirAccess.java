package tinyjsondb.access;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * That's an generic DAO.
 */
public interface DirAccess {
    
    /**
     * Load or update workspace if needed
     * @param workspace -
     * @param force false: update (or load if needed), true: load in new folder
     */
    void initWorkspace(Workspace workspace, boolean force);
    
    /**
     * alle Workspaces ermitteln
     * @param folder parent folder
     * @return folder names, without parent path
     */
    List<String> getAllFolders(String folder);

    /**
     * Load all files with contents from given folder.
     * @param folder relative filename in workspace
     * @return map with key=filename, value=content
     */
    Map<String,String> loadAllFiles(String folder);
    
    /**
     * Load many plain text files
     * @param filenames relative filenames in workspace
     * @return map with key=filename, value=content. null as content if file does not exist.
     */
    Map<String,String> loadFiles(Set<String> filenames);

    /**
     * @param files map with key=filename, value=content. null as content deletes file.
     * @param commitMessage -
     * @param workspace -
     */
    void saveFiles(Map<String, String> files, CommitMessage commitMessage, Workspace workspace);

    /**
     * @param filenames zu löschende Ordner (Eintrag endet mit '/*') oder Dateien
     * @param commitMessage -
     * @param workspace -
     * @param cantBeDeleted not null, nach dem Aufruf sind dadrin die Dateien, die nicht gelöscht werden konnten
     */
    void deleteFiles(Set<String> filenames, CommitMessage commitMessage, Workspace workspace, List<String> cantBeDeleted);

    /**
     * @param files changed or moved files
     * @param commitMessage -
     * @param workspace -
     */
    void moveFiles(List<IMoveFile> files, CommitMessage commitMessage, Workspace workspace);

    /**
     * Create and push new branch. Does not switch to the branch.
     * @param workspace workspace of old branch
     * @param newBranch name of new branch
     * @param commit place where to branch, commit hash or tag in old branch, can be null
     */
    void createBranch(Workspace workspace, String newBranch, String commit);
    
    /**
     * @param workspace -
     * @return all branch names
     */
    List<String> getBranchNames(Workspace workspace);

    /**
     * Merge source branch into target branch.
     * @param sourceBranch branch name, e.g. "feature-4711"
     * @param targetBranch branch name, often "master"
     * @param login -
     */
    void mergeBranch(String sourceBranch, String targetBranch, String login);
    
    /**
     * @param workspace -
     * @return CommitHash of given workspace, not null
     */
    CommitHash getCommitHash(Workspace workspace);
}
