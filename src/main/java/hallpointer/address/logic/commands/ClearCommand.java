package hallpointer.address.logic.commands;

import static java.util.Objects.requireNonNull;

import hallpointer.address.model.HallPointer;
import hallpointer.address.model.Model;

/**
 * Clears the CCA system.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";

    public static final String MESSAGE_SUCCESS = "CCA system has been cleared!";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.setHallPointer(new HallPointer());
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
