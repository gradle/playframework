package org.gradle.playframework.tools.internal.javascript;

import org.gradle.api.internal.TaskOutputsInternal;

import java.io.File;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class SimpleStaleClassCleaner extends StaleClassCleaner {
    private final Set<File> filesToDelete;
    private final Set<File> toClean = new HashSet<>();
    private final Set<String> prefixes = new HashSet<>();
    private final Queue<File> directoriesToDelete = new PriorityQueue<>(10, new NaturalReverseComparator());
    private boolean didWork;

    public SimpleStaleClassCleaner(TaskOutputsInternal taskOutputs) {
        this(taskOutputs.getPreviousOutputFiles());
    }

    public SimpleStaleClassCleaner(Set<File> filesToDelete) {
        this.filesToDelete = filesToDelete;
    }

    @Override
    public void addDirToClean(File toClean) {
        this.toClean.add(toClean);
        prefixes.add(toClean.getAbsolutePath() + File.separator);
    }

    @Override
    public void execute() {
        for (File f : filesToDelete) {
            for (String prefix : prefixes) {
                if (f.getAbsolutePath().startsWith(prefix) && f.isFile()) {
                    didWork |= f.delete();
                    markParentDir(f);
                }
            }
        }
        while (!directoriesToDelete.isEmpty()) {
            File directory = directoriesToDelete.poll();
            if (isEmpty(directory)) {
                didWork |= directory.delete();
                markParentDir(directory);
            }
        }
    }

    private void markParentDir(File f) {
        File parentDir = f.getParentFile();
        if (parentDir != null && !toClean.contains(parentDir)) {
            directoriesToDelete.add(parentDir);
        }
    }

    private boolean isEmpty(File parentDir) {
        String[] children = parentDir.list();
        return children != null && children.length == 0;
    }

    public boolean getDidWork() {
        return didWork;
    }

    private static class NaturalReverseComparator implements Comparator<File> {

        @Override
        public int compare(File o1, File o2) {
            return -o1.getName().compareTo(o2.getName());
        }
    }
}