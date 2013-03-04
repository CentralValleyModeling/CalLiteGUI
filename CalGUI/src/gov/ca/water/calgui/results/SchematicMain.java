/**
 * 
 */
package gov.ca.water.calgui.results;

import gov.ca.water.calgui.MainMenu;

import java.awt.Toolkit;
import java.awt.geom.AffineTransform;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.batik.script.Window;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.JSVGScrollPane;
import org.apache.batik.swing.gvt.GVTTreeRendererAdapter;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.svg.SVGLoadEventDispatcherAdapter;
import org.apache.batik.swing.svg.SVGLoadEventDispatcherEvent;
import org.swixml.SwingEngine;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

/**
 * SchematicMain: Class to handle display of SVG-formatted schematic view.
 * 
 * @author tslawecki
 * 
 * 
 * 
 */
public class SchematicMain {

	JSVGCanvas canvas;
	Document document;
	Window window;
	MainMenu mainMenu;
	SwingEngine swix;

	AffineTransform theAT = new AffineTransform(4.0, 0, 0.0, 4.0, -1400.0, -200.0);

	public void setAffineTransform(double m0, double m1, double m2, double m3, double m4, double m5) {

		AffineTransform t = new AffineTransform(m0, m1, m2, m3, m4, m5);
		canvas.setRenderingTransform(t);

	}

	/**
	 * 
	 * @param p
	 *            Housing panel
	 * @param url
	 *            URL for SVG file
	 * @param mainMenuIn
	 *            Handle to main panel - used to access information about loaded scenarios
	 * @param swix
	 *            Handle to UI
	 */
	public SchematicMain(JPanel p, String url, final MainMenu mainMenuIn, SwingEngine swix) {

		mainMenu = mainMenuIn;
		this.swix = swix;
		canvas = new JSVGCanvas();

		// Forces the canvas to always be dynamic even if the current
		// document does not contain scripting or animation.
		canvas.setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);

		canvas.setEnablePanInteractor(true);
		canvas.setEnableZoomInteractor(true);
		canvas.setURI(url);

		System.out.println(canvas.getURI());

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
			}
		});

		canvas.addGVTTreeRendererListener(new GVTTreeRendererAdapter() {

			@Override
			public void gvtRenderingCompleted(GVTTreeRendererEvent e) {
				super.gvtRenderingCompleted(e);
				canvas.setRenderingTransform(theAT, true);
			}

		});

		p.add("Center", new JSVGScrollPane(canvas));

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
			theAT = new AffineTransform(4.0, 0, 0.0, 4.0, -1400.0, -200.0);
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

			if (label == null)
				Toolkit.getDefaultToolkit().beep();
			else {
				JList lstScenarios = null;
				JFrame desktop = null;
				if (mainMenu.lstScenarios.getModel().getSize() == 0)
					JOptionPane.showMessageDialog(null, "No scenarios loaded", "Error", JOptionPane.ERROR_MESSAGE);
				else
					desktop = (JFrame) swix.find("desktop");
				lstScenarios = (JList) swix.find("SelectedList");
				DSSGrabber dss_Grabber;
				dss_Grabber = new DSSGrabber(lstScenarios);
				DisplayFrame.displayFrame(DisplayFrame.QuickState(swix) + ";Locs-" + label + ";Index-" + "SchVw" + label, swix,
				        dss_Grabber, lstScenarios, desktop, 0);
			}

			System.out.println("Title: " + label);

			// if (tag.startsWith("polygon") || tag.startsWith("line")) {
			// NodeList childNodes = el.getParentNode().getChildNodes();
			// for (int i = 0; i < childNodes.getLength(); i++) {
			// Node item = childNodes.item(i);
			// if (item instanceof Element) {
			// Element ce = (Element) item;
			// if (ce.getTagName().equals("text")) {
			// label = ce.getTextContent();
			// }
			// }
			// }
			// } else if (tag.startsWith("text")) {
			// label = el.getTextContent();
			// } else if (tag.startsWith("tspan")) {
			// label = el.getParentNode().getTextContent();
			// }
			// if (label != null) {
			// window.alert(tag + ":" + label);
			// }
		}
	}

}
