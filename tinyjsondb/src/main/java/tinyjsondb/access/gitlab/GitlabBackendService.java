package tinyjsondb.access.gitlab;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.pmw.tinylog.Logger;

import minerva.access.CommitMessage;
import minerva.access.DirAccess;
import minerva.base.StringService;
import minerva.base.UserMessage;
import minerva.config.BackendService;
import minerva.config.ICommit;
import minerva.config.MinervaConfig;
import minerva.model.BookSO;
import minerva.model.SeiteSO;
import minerva.model.WorkspaceSO;
import minerva.model.WorkspacesSO;
import minerva.persistence.gitlab.git.GitService;
import minerva.seite.Seite;
import minerva.user.User;

public class GitlabBackendService implements BackendService {
    private final MinervaConfig config;
    private final GitlabSystem gitlabSystem;
    private final GitlabRepository repo;

    public GitlabBackendService(MinervaConfig config) {
        this.config = config;
        gitlabSystem = new GitlabSystem(config.getGitlabUrl());
        repo = new GitlabRepository(gitlabSystem, config.getGitlabProject());
    }

    @Override
    public String getInfo(String lang) {
        return "Gitlab (" + repo.getProjectUrl() + ")";
    }

    @Override
    public DirAccess getDirAccess() {
        return new GitlabDirAccess(repo);
    }

    @Override
    public boolean withPassword() {
        return true;
    }

    @Override
    public User login(String login, String password, String pMail) {
        if (StringService.isNullOrEmpty(login) || StringService.isNullOrEmpty(password)) {
            return null;
        }
        Logger.info(login + " | GitlabBackendService.login");
        User user = new User();
        user.setLogin(login);
        GitlabDataStore xu = new GitlabDataStore(user);
        xu.setPassword(password);
        String mail = gitlabSystem.login(user);
        if (mail == null) {
            mail = pMail;
        }
        if (mail == null) {
            return null;
        }
        user.setMailAddress(mail);
        return user;
    }
    
    @Override
    public String getUserFolder(User user) {
    	String folder = user.getLogin();
        Logger.debug(user.getLogin() + " | folder: " + folder);
    	return folder;
    }

    @Override
    public void uptodatecheck(WorkspaceSO workspace, UpdateAction updateAction) {
        File workspaceFolder = new File(workspace.getFolder());
        User user = workspace.getUser().getUser();
        boolean areThereRemoteUpdates = new GitService(workspaceFolder).areThereRemoteUpdates(workspace.getBranch(), user);
        if (areThereRemoteUpdates) {
            workspace.pull();
            updateAction.update();
        }
    }

    @Override
    public Seite forceReloadIfCheap(String filenameMeta) {
        return null; // It's expensive. Do not update page.
    }

    @Override
    public List<String> getAddableBranches(WorkspacesSO workspaces, WorkspaceSO ref) {
        ref.pull();
        List<String> ret = repo.getBranches(ref);
        ret.removeIf(branch -> branch.toLowerCase().contains(WorkspacesSO.MINERVA_BRANCH));
        for (WorkspaceSO w : workspaces) {
            ret.remove(w.getBranch());
        }
        return ret;
    }

    @Override
    public void saveFiles(CommitMessage commitMessage, WorkspaceSO workspace, Set<String> addFilenames,
            Set<String> removeFilenames) {
        repo.push(commitMessage, workspace, addFilenames, removeFilenames, () -> {});
    }

    @Override
    public String getMergeRequestPath(Long id) {
        return repo.getProjectUrl() + config.getGitlabMergeRequestPath()
            + id;
    }

    @Override
    public String getCommitLink(String hash) {
        return repo.getProjectUrl() + config.getGitlabCommitPath() // http://host:port/user/repo/-/commit/
            + hash;
    }

    @Override
    public List<ICommit> getSeiteMetaHistory(SeiteSO seite, boolean followRenames) {
		return getHistory(seite.gitFilenameMeta(), seite.getBook(), followRenames);
	}

	protected final List<ICommit> getHistory(String filename, BookSO book, boolean followRenames) {
    	return repo.getFileHistory(filename, followRenames, book.getWorkspace());
    }

    @Override
    public List<ICommit> getHtmlChangesHistory(WorkspaceSO workspace, int start, int size) {
        return repo.getHtmlChangesHistory(workspace, start, size);
    }

    @Override
    public String logout(User user) {
        if (GitFactory.logout(user)) {
            return "Gitlab revoke ok";
        }
        return "";
    }

    @Override
    public void saveAll(CommitMessage commitMessage, WorkspaceSO workspace) {
        Set<String> add = new TreeSet<>();
        add.add(GitService.ADD_ALL_FILES);
        repo.push(commitMessage, workspace, add, new TreeSet<>(), () -> {});
    }

    @Override
    public void checkIfMoveIsAllowed(WorkspaceSO workspace) {
        if (workspace.getUser().getUser().getDelayedPush().contains(workspace.getBranch())) {
            // User will loose Git history. So better end f-s mode before moving a page.
            throw new UserMessage("moveNotAllowedForFSMode", workspace);
        }
    }
}
