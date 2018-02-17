package typeOnNotebook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandRunner {
	/**
	 * Runs a system command and returns its output
	 * 
	 * @param command
	 *            command to be executed
	 * @return command's output or null on IOException
	 */
	public static String getOutput(final String command) {
		String commandOuputString = "";
		try {
			Process p = Runtime.getRuntime().exec(command);
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				commandOuputString += line + '\n';
			}
		} catch (IOException e) {
			return null;
		}

		return commandOuputString;
	}
}
