package de.galan.plunger.util;

import static org.fusesource.jansi.Ansi.*;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import org.fusesource.jansi.Ansi.Color;


/**
 * Prints output to the stdout/err, colors can be used via AnsiConsole
 *
 * @author daniel
 */
public class Output {

	private static boolean colors = true;
	private static PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(java.io.FileDescriptor.out),
	StandardCharsets.UTF_8), 4096));
	// 10k local
	// with sout: 340s
	// with buffer: 12s


	public static void setColor(boolean colors) {
		Output.colors = colors;
	}


	public static void print(Color color, String text) {
		out.print(colors ? ansi().fg(color).a(text).reset() : text);
		// out.print(text);
	}


	public static void println(Color color, String text) {
		out.println(colors ? ansi().fg(color).a(text).reset() : text);
		// out.println(text);
	}


	public static void print(String line) {
		out.print(line);
	}


	public static void println(String line) {
		out.println(line);
	}


	public static void error(String text) {
		System./**/err.println(colors ? ansi().fg(Color.RED).a(text).reset() : text);
	}


	public static void flush() {
		out.flush();
	}
}
