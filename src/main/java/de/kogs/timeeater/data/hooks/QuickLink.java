/**
 *
 */
package de.kogs.timeeater.data.hooks;

import de.kogs.timeeater.data.JobVo;

import java.awt.Desktop;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 */
public class QuickLink {
	private String pattern;
	private String url;
	
	public QuickLink () {
	
	}
	
	public QuickLink (String pattern, String url) {
		this.pattern = pattern;
		this.url = url;
	}
	
	public String getPattern() {
		return pattern;
	}
	
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public void fireForJob(JobVo job) {
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
		return new URL(url);
	}
	
	@Override
	public String toString() {
		return "QuickLink [pattern=" + pattern + ", url=" + url + "]";
	}
	
}
