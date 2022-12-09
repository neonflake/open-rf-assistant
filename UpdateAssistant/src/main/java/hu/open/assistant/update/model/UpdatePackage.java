package hu.open.assistant.update.model;

/**
 * Logical representation of an update package. The update package physically is a .zip file and was created for a
 * specific program (target). The update package has a version and filename.
 */
public class UpdatePackage implements Comparable<UpdatePackage> {

    private final UpdateTarget program;
    private final double version;
    private final String filename;

    public UpdatePackage(UpdateTarget program, double version, String filename) {
        this.program = program;
        this.version = version;
        this.filename = filename;
    }

    public UpdateTarget getProgram() {
        return program;
    }

    public double getVersion() {
        return version;
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public int compareTo(UpdatePackage other) {
        return Double.compare(other.getVersion(), version);
    }
}
