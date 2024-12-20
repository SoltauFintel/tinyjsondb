package tinyjsondb.minerva3;

import java.util.List;

/**
 * User hängt direkt unter root
 * 
 * DTO
 * Persistenz
 */
public class User {
    private String id;
    private String login;
    private String name;
    
    public List<Journal> getJournals() {
        return null;
    }

    public List<Workspace> getWorkspaces() {
        return null;
    }

    public class Journal {

        public List<JBranch> getJBranchs() {
            return null;
        }

        public class JBranch {
        }
    }
    
    public class Workspace {
        private String branch;
        
        public List<Book> getBooks() {
            return null;
        }
        
        public class Book {

            public List<Seite> getSeiten() {
                return null;
            }
            
            public class Seite {
                private String meta;
                private String deHtml;
                private String enHtml;
            
                public List<Comment> getComments() {
                    return null;
                }

                public List<Image> getImages() {
                    return null;
                }

                public class Comment {
                }
                
                public class Image {
                }
            }
        }
        
        public class ExportTemplateSet {
            // felder
        }
        
        public class Values {
            // name, tecKey
            // -> key value pairs
        }
    }
}
