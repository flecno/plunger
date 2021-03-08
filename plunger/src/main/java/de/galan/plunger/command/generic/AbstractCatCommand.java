package de.galan.plunger.command.generic;

import static org.apache.commons.lang3.StringUtils.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableLong;
import org.fusesource.jansi.Ansi.Color;

import de.galan.plunger.command.AbstractCommand;
import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.CompleteMessageMarshaller;
import de.galan.plunger.domain.Message;
import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.util.Output;


/**
 * Generic cat command, that handles most of the plunger arguments already.
 */
public abstract class AbstractCatCommand extends AbstractCommand {

	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		if (pa.getTarget().isDestinationErased()) {
			throw new CommandException("No destination has been specified");
		}
	}


	@Override
	public void process(PlungerArguments pa) throws CommandException {
		Message message = null;
		boolean firstMessage = true;
		MutableLong counter = new MutableLong();
		Long limit = pa.getCommandArgumentLong("n");
		beforeFirstMessage(pa);
		while(!isLimitExceeded(limit, counter) && (message = getNextMessage(pa)) != null) {
			firstMessage = printSeparator(firstMessage, pa);
			printMessage(pa, message);
		}
	}


	@SuppressWarnings("unused")
	protected void beforeFirstMessage(PlungerArguments pa) throws CommandException {
		// can be overriden
	}


	protected abstract Message getNextMessage(PlungerArguments pa) throws CommandException;


	protected boolean isLimitExceeded(Long limit, MutableLong counter) {
		boolean result = false;
		if (limit != null) {
			if (counter.longValue() >= limit) {
				result = true;
			}
			counter.increment();
		}
		return result;
	}


	protected boolean printSeparator(boolean firstMessage, PlungerArguments pa) {
		if (!firstMessage) {
			if (!pa.containsCommandArgument("e") && !pa.containsCommandArgument("d")) {
				Output.println(StringUtils.repeat("-", 64));
			}
		}
		return false;
	}


	protected void printMessage(PlungerArguments pa, Message message) {
		boolean excludeBody = pa.containsCommandArgument("b");
		if (excludeBody) {
			message.setBody(null);
		}
		else {
			Long cut = pa.getCommandArgumentLong("c"); // limiting the body output
			if (cut != null) {
				boolean addDots = StringUtils.length(message.getBody()) > cut;
				String cutted = StringUtils.substring(message.getBody(), 0, cut.intValue()) + (addDots ? "..." : "");
				message.setBody(cutted);
			}
		}

		if (pa.containsCommandArgument("e")) {
			String[] marshalled = new CompleteMessageMarshaller().marshalParts(message);
			Output.print(Color.GREEN, marshalled[0]);
			Output.print(marshalled[1]);
			Output.println(Color.YELLOW, marshalled[2]);
		}
		else {
			List<String> keys = new ArrayList<>(message.getProperties().keySet());
			Collections.sort(keys);
			for (String key: keys) {
				Color colorKey = isSystemHeader(key) ? Color.BLUE : Color.GREEN;
				Output.print(colorKey, key + ":");
				Output.println(SPACE + message.getPropertyString(key));
			}
			if (StringUtils.isNotBlank(message.getBody())) {
				Output.println(Color.YELLOW, message.getBody());
			}
		}
		Output.flush();
	}


	protected abstract boolean isSystemHeader(String headerName);

}
