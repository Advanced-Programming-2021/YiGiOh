package edu.sharif.ce.apyugioh.view;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciithemes.u8.U8_Grids;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;
import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.model.User;

import java.util.List;

public class ScoreboardView extends View {

    public void showScoreboard(List<User> users) {
        AsciiTable scoreboard = new AsciiTable();
        scoreboard.addRule();
        scoreboard.addRow(null, null, "Scoreboard");
        scoreboard.addStrongRule();
        scoreboard.addRow("rank", "nickname", "score");
        scoreboard.addStrongRule();
        int counter = 0;
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            scoreboard.addRow(i - counter + 1, user.getNickname(), user.getScore());
            if (i < users.size() - 1) {
                User nextUser = users.get(i + 1);
                if (user.getScore() == nextUser.getScore()) counter++;
                else counter = 0;
            }
            scoreboard.addRule();
        }
        scoreboard.setTextAlignment(TextAlignment.CENTER);
        scoreboard.getContext().setGrid(U8_Grids.borderStrongDoubleLight());
        System.out.println(scoreboard.render(Math.max(80, ProgramController.getReader().getTerminal().getWidth())));
    }
}
