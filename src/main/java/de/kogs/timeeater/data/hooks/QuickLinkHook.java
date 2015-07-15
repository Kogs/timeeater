/**
 *
 */
package de.kogs.timeeater.data.hooks;

import de.kogs.timeeater.data.Job;

import java.awt.Desktop;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 */
public class QuickLinkHook extends Hook {
	

	public QuickLinkHook () {
	}

	public QuickLinkHook (Job job, URL link) {
		super(job);
		getProperties().put("link", link.toString());
	}
	
	/* (non-Javadoc)
	 * @see de.kogs.timeeater.data.hooks.Hook#action()
	 */
	@Override
	public void action() {
		try {
			openWebpage(new URL(getLink(), job.getName()));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	private static void openWebpage(URI uri) {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(uri);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void openWebpage(URL url) {
		try {
			openWebpage(url.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	private URL getLink() throws MalformedURLException {
		return new URL(getProperties().get("link"));
	}

	
}
