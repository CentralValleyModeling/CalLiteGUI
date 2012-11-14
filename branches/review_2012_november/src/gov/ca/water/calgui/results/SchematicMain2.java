package gov.ca.water.calgui.results;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFrame;

import org.apache.batik.script.Window;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.JSVGScrollPane;
import org.apache.batik.swing.svg.SVGLoadEventDispatcherAdapter;
import org.apache.batik.swing.svg.SVGLoadEventDispatcherEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

public class SchematicMain2 {
	public static void main(String[] args) {
		new SchematicMain2();
	}

	JFrame frame;
	JSVGCanvas canvas;
	Document document;
	Window window;

	public SchematicMain2() {
		frame = new JFrame();
		canvas = new JSVGCanvas();
		// Forces the canvas to always be dynamic even if the current
		// document does not contain scripting or animation.
		canvas.setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);
		canvas.addSVGLoadEventDispatcherListener(new SVGLoadEventDispatcherAdapter() {
			@Override
			public void svgLoadEventDispatchStarted(SVGLoadEventDispatcherEvent e) {
				// At this time the document is available...
				document = canvas.getSVGDocument();
				// ...and the window object too.
				window = canvas.getUpdateManager().getScriptingEnvironment().createWindow();
				// Registers the listeners on the document
				// just before the SVGLoad event is
				// dispatched.
				registerListeners();
				// It is time to pack the frame.
				frame.pack();
				frame.setSize(800, 600);
				frame.setVisible(true);
			}
		});

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				File f = new File("Config/callite_sample.svg");
				System.out.println(f.getAbsolutePath());
				canvas.setURI("file:///" + f.getAbsolutePath());
			}
		});
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(new JSVGScrollPane(canvas));
		frame.setSize(800, 600);
		frame.setVisible(true);
	}

	public void registerListeners() {
		// Gets an element from the loaded document.
		NodeList elementsByTagName = document.getElementsByTagName("g");
		for (int i = 0; i < elementsByTagName.getLength(); i++) {

			Element elt = (Element) elementsByTagName.item(i);
			EventTarget t = (EventTarget) elt;
			// Adds a 'onclick' listener
			t.addEventListener("click", new OnClickAction(), false);
		}
	}

	public class OnClickAction implements EventListener {
		@Override
		public void handleEvent(Event evt) {
			// Perform some actions here...
			evt.stopPropagation();
			// ...for example schedule an action for later:
			window.setTimeout(new DisplayClickedLabelTask(evt), 500);
		}
	}

	public class DisplayClickedLabelTask implements Runnable {
		private final Event evt;

		public DisplayClickedLabelTask(Event evt) {
			this.evt = evt;
		}

		@Override
		public void run() {

			String label = null;
			Element el = ((Element) evt.getTarget());
			String tag = el.getTagName();
			System.out.println("Clicked on: " + evt.getTarget() + " " + tag);
			Element pel = el;

			// Get first text element in containing group

			while (label == null && pel.getParentNode() != null && pel.getParentNode() instanceof Element) {

				pel = (Element) pel.getParentNode();
				String ptag = pel.getTagName();
				if (ptag.equals("g")) {
					// When first group is found, look for first text element
					NodeList childNodes = pel.getChildNodes();
					for (int i = 0; (label == null) && (i < childNodes.getLength()); i++) {
						Node item = childNodes.item(i);
						if (item instanceof Element) {
							Element ce = (Element) item;
							System.out.println("ce = tag:" + ce.getTagName() + " content: " + ce.getTextContent());
							if (ce.getTagName().startsWith("text")) {
								label = ce.getTextContent();
							}
						}
					}
				}

			}
			if (label != null) {
				window.alert(label);
			}
		}
	}
}
