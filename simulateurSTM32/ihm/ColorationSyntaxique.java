/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ensea.simulateurSTM32.ihm;

import ensea.simulateurSTM32.simulateurstm32.MainFrame;
import java.awt.Color;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 *  Guillaume - Vincent
 * Permet la coloration Syntaxique dans la fenêtre d'édition 
 */
public abstract class ColorationSyntaxique extends DefaultStyledDocument
{
	public JTextComponent Pere;
	public String[] words;
	public Style[] styles;
	public Style normal;
	public StyleContext sc = new StyleContext();

	public ColorationSyntaxique(JTextComponent comp)
	{
		Pere=comp;
		initStyle();
	}

	public abstract void initStyle();

	@Override
	public void insertString(int arg0, String arg1, AttributeSet arg2) throws BadLocationException
	{
		super.insertString(arg0, arg1, arg2);
		colorise(Pere);
	}

	@Override
	public void remove(int arg0, int arg1) throws BadLocationException
	{
		super.remove(arg0, arg1);
		colorise(Pere);
	}

	public void colorise(JTextComponent comp) throws BadLocationException
	{
		//System.out.println("Colorise");
                if(words.length<styles.length) throw new ArrayIndexOutOfBoundsException();

		String content = null;
		Pattern pattern;
		Matcher matcher;

		try {
			Document d = comp.getDocument();
			content = d.getText(0, d.getLength());
		} catch (BadLocationException e) {
			return;
		}


		((DefaultStyledDocument)comp.getDocument()).setCharacterAttributes(0, content.length(), normal, true);

		for(int i=0;i<words.length;i++)
		{
			pattern = Pattern.compile(words[i],Pattern.MULTILINE);
			matcher = pattern.matcher(content);

			while(matcher.find())
			{
                            DefaultStyledDocument defaultStyledDocument = ((DefaultStyledDocument)comp.getDocument());
							defaultStyledDocument.setCharacterAttributes(matcher.start(), matcher.end()-matcher.start(), styles[i], true);
                            System.out.println(styles[i].getName());
			}
		}
	}

	public static class styleBin extends ColorationSyntaxique
	{
		public styleBin(JTextPane comp)
		{
			super(comp);

		}

		@Override
		public void initStyle()
		{
			int index=0;
			words = new String[]
			{
				"[01]+"
			};

			styles = new Style[1];
			normal = sc.addStyle("normal", null);
			normal.addAttribute(StyleConstants.Foreground, new Color(0,0,0));
			normal.addAttribute(StyleConstants.FontSize, 14);
			normal.addAttribute(StyleConstants.FontFamily, "Courier New");
			normal.addAttribute(StyleConstants.Bold, false);

			styles[index]= sc.addStyle("binaire", null);
			styles[index].addAttribute(StyleConstants.FontSize,14);
			styles[index].addAttribute(StyleConstants.FontFamily, "Courier New");
			styles[index++].addAttribute(StyleConstants.Bold, true);

		}
	}

	public static class styleVar extends ColorationSyntaxique
	{
		public styleVar(JTextPane comp)
		{
			super(comp);
		}

		@Override
		public void initStyle()
		{
			int index=0;
			words = new String[]
			{
				"\\b(?:SPACE|DCD|EQU)\\b",
				"^[a-zA-Z]\\w*\\W|^[a-zA-Z]\\w*$",
				"\\s[0-9]+",
				"//.*\\n|//.*$"
			};

			styles = new Style[4];
			normal = sc.addStyle("normal", null);
			normal.addAttribute(StyleConstants.Foreground, new Color(0,0,0));
			normal.addAttribute(StyleConstants.FontSize, 14);
			normal.addAttribute(StyleConstants.FontFamily, "Courier New");
			normal.addAttribute(StyleConstants.Bold, false);

			styles[index]= sc.addStyle("controle", null);
			styles[index].addAttribute(StyleConstants.Foreground, new Color(0,0,192));
			styles[index].addAttribute(StyleConstants.FontSize, 14);
			styles[index].addAttribute(StyleConstants.FontFamily, "Courier New");
			styles[index++].addAttribute(StyleConstants.Bold, true);

			styles[index]= sc.addStyle("etiquette", null);
			styles[index].addAttribute(StyleConstants.Foreground, new Color(128,0,128));
			styles[index].addAttribute(StyleConstants.FontSize, 14);
			styles[index++].addAttribute(StyleConstants.FontFamily, "Courier New");

			styles[index]= sc.addStyle("chiffre", null);
			styles[index].addAttribute(StyleConstants.Foreground, new Color(0,128,0));
			styles[index].addAttribute(StyleConstants.FontSize, 14);
			styles[index++].addAttribute(StyleConstants.FontFamily, "Courier New");

			styles[index]= sc.addStyle("comentaire", null);
			styles[index].addAttribute(StyleConstants.Foreground, new Color(0,0,0));
			styles[index].addAttribute(StyleConstants.FontSize, 14);
			styles[index].addAttribute(StyleConstants.Background, new Color(192,192,192));
			styles[index++].addAttribute(StyleConstants.FontFamily, "Courier New");

		}
	}
}