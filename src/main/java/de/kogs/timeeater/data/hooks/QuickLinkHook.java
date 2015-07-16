/**
 *
 */
package de.kogs.timeeater.data.hooks;

import de.kogs.timeeater.data.Job;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.Node;

import java.awt.Desktop;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 */
@HookConfig(singleton = true)
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
	public boolean action() {
		try {
			openWebpage(new URL(getLink(), job.getName()));
			return true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return false;
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

	/* (non-Javadoc)
	 * @see de.kogs.timeeater.data.hooks.Hook#getGuiContent()
	 */
	@Override
	public HookConfigGui getGuiContent() {
		return new HookConfigGui() {
			
			@Override
			public BooleanBinding submitSupportedBinding() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void submit() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public Node getGui() {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		try {
			return "QuickLink to " + getLink().toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return "QuickLink";
	}
	
}
