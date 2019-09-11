package com.biit.form.runner.common;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.biit.utils.image.ImageTools;
import com.biit.vaadin.components.images.ImagePanel;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;

public class ImagePreview extends ImagePanel {
	private static final long serialVersionUID = -2464554146921469606L;
	private static ByteArrayInputStream defaultImage = null;
	private StreamSource streamSource = null;

	public ImagePreview() {
		super(1f);
	}

	public ImagePreview(float resize) {
		super(resize);
	}

	@Override
	protected StreamSource getImage() {
		if (streamSource != null) {
			return streamSource;
		}
		return new DefaultImageSource();
	}

	/**
	 * Create a StreamSource from a form flow image as byte array.
	 */
	private class DefaultImageSource implements StreamResource.StreamSource {
		private static final long serialVersionUID = -324235946884266131L;

		public DefaultImageSource() {
		}

		@Override
		public InputStream getStream() {
			if (defaultImage == null) {
				defaultImage = new ByteArrayInputStream(ImageTools.createDefaultImage((int) getWidth(), (int) getHeight(), ""));
			}
			defaultImage.reset();
			return defaultImage;

		}
	}

	public void setStreamSource(StreamSource streamSource) {
		this.streamSource = streamSource;
		// Reset zoom when change the image.
		setResize(1f);
		redraw();
	}

}