package edu.sharif.ce.apyugioh.controller;

import lombok.Getter;
import lombok.Setter;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.*;

public class DynamicCompleter implements Completer {

    @Getter
    @Setter
    private List<Completer> completers;

    {
        completers = new ArrayList<>();
    }

    public DynamicCompleter(Completer... completers) {
        this(Arrays.asList(completers));
    }

    public DynamicCompleter(List<Completer> completers) {
        assert completers != null;
        this.completers.addAll(completers);
    }

    public void addCompleters(Completer... completers) {
        this.completers.addAll(Arrays.asList(completers));
    }

    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        Objects.requireNonNull(line);
        Objects.requireNonNull(candidates);
        for (Completer c : this.completers) {
            c.complete(reader, line, candidates);
        }
    }
}
