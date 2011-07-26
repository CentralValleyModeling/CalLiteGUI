/**
 * 
 */
package com.limno.calgui.results;

/**
 * @author tslawecki
 *
 */

import java.awt.geom.AffineTransform;

import javax.swing.JFrame;
import javax.swing.JPanel;

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

public class SchematicMain {

	JSVGCanvas canvas;
	Document document;
	Window window;

	public void setAffineTransform(double m0,double m1,double m2,double m3,double m4,double m5) {
	
		AffineTransform t = new AffineTransform(m0,m1,m2,m3,m4,m5);
		canvas.setRenderingTransform(t);
	}
	
	public SchematicMain(JPanel p, String url) {

		canvas = new JSVGCanvas();

		// Forces the canvas to always be dynamic even if the current
		// document does not contain scripting or animation.
		canvas.setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);

		canvas.setEnablePanInteractor(true);
		canvas.setEnableZoomInteractor(true);
		canvas.setURI(url);
		
		canvas.addSVGLoadEventDispatcherListener(new SVGLoadEventDispatcherAdapter() {
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

		p.add("Center",new JSVGScrollPane(canvas));
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
		public void handleEvent(Event evt) {
			// Perform some actions here...
			evt.stopPropagation();
			// ...for example schedule an action for later:
			window.setTimeout(new DisplayClickedLabelTask(evt), 500);
		}
	}

	public class DisplayClickedLabelTask implements Runnable {
		private Event evt;

		public DisplayClickedLabelTask(Event evt) {
			this.evt = evt;
		}

		public void run() {
			
			AffineTransform t = canvas.getViewBoxTransform();
			double[] m = new double[6];
			t.getMatrix(m);
			for (int i = 0; i<6; i++) System.out.println (m[i]);
			System.out.println(m);

			String label = null;
			
			System.out.println("Clicked on: " + evt.getTarget());
			Element el = ((Element) evt.getTarget());
			String tag = el.getTagName();
			evt.stopPropagation();
			if (tag.startsWith("polygon") || tag.startsWith("line")) {
				NodeList childNodes = el.getParentNode().getChildNodes();
				for (int i = 0; i < childNodes.getLength(); i++) {
					Node item = childNodes.item(i);
					if (item instanceof Element) {
						Element ce = (Element) item;
						if (ce.getTagName().equals("text")) {
							label = ce.getTextContent();
						}
					}
				}
			} else if (tag.startsWith("text")) {
				label = el.getTextContent();
			} else if (tag.startsWith("tspan")) {
				label = el.getParentNode().getTextContent();
			}
			if (label != null) {
				window.alert(label);
			}
		}
	}

}
