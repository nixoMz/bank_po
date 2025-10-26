import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Stream;

public class FileManager {

    private final   String  fileName;
    private final   Path    path;
    private final   String  directory;

    private         boolean createAllowed;
    private         boolean overwriteAllowed;
    private         boolean deleteAllowed;

    private         int index;


    public FileManager(String fileName, String directory) {
        this.fileName         = fileName;
        this.directory        = directory;

        this.createAllowed    = false;
        this.overwriteAllowed = false;
        this.deleteAllowed    = false;

        this.index            = 0;

        this.path = Paths.get(directory, fileName);
    }

    public FileManager(String fileName) {
        this.fileName         = fileName;
        this.directory        = null;

        this.createAllowed    = false;
        this.overwriteAllowed = false;
        this.deleteAllowed    = false;

        this.index            = 0;

        this.path = Paths.get(fileName);
    }


    public int write(String line) throws IOException {
        if(!isExist()){
            if(createAllowed){
                createFile();
            } else {
                return -1;
            }
        }
        Files.writeString(this.path, line, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        return 0;

    }
    public int writeln(String line) throws IOException {
        if(!isExist()){
            if(createAllowed){
                createFile();
            } else {
                return -1;
            }
        }
        Files.writeString(this.path, line + "\n", StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        return 0;
    }

    public Optional<String> read() throws IOException {
        try (Stream<String> fileLines =  Files.lines(path, StandardCharsets.UTF_8)) {
            return fileLines.skip(this.index).findFirst();
        }
    }
    public Optional<String> readln() throws IOException {
        try (Stream<String> fileLines =  Files.lines(path, StandardCharsets.UTF_8)) {
            Optional<String> line = fileLines.skip(this.index).findFirst();

            if(line.isPresent()){
                index++;
            } else {
                this.index = 0;
            }

            return line;
        }
    }
    public String readAll() throws IOException {
        return Files.readString(this.path, StandardCharsets.UTF_8);
    }

    public boolean isExist() {
        return Files.isRegularFile(this.path);
    }

    public int createFile() throws IOException {
        if(this.isExist()) {
            return 1;
        }

        if(this.directory == null){
            Files.createFile(this.path);
        } else{
            Files.createDirectories(Paths.get(this.directory));
            Files.createFile(this.path);
        }
        return 0;
    }
    public int deleteFile() throws IOException {
        if(!this.deleteAllowed) {
            return -1;
        } else if(!this.isExist()) {
            return -2;
        }
        Files.delete(this.path);
        return 0;
    }
    public int rewriteFile() throws IOException {
        int result = -10;

        result = this.deleteFile();

        if(result < 0) return result;

        result = this.createFile();

        return result;
    }

    public void allowCreate(){
        this.createAllowed = true;
    }
    public void allowOverwrite(){
        this.overwriteAllowed = true;
    }
    public void allowDelete(){
        this.deleteAllowed = true;
    }
}
