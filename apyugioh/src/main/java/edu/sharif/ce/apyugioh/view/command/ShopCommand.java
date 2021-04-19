package edu.sharif.ce.apyugioh.view.command;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "shop", mixinStandardHelpOptions = true, description = "game shop commands")
public class ShopCommand {

    @Command(name = "buy", description = "buy game cards")
    public void buy(@Parameters(index = "0", description = "card name") String name) {
        System.out.println("buy " + name);
    }

    @Command(name = "show", description = "show all cards")
    public void show(@Option(names = {"-a", "--all"}, description = "isAll") boolean isAll) {
        System.out.println("show");
    }

}
