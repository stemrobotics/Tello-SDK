package tello.server.utils;

import tello.server.constant.ServerConstant;

public class ServerUtil {
    private ServerUtil() {
	}

    /**
     * Returns the extension of a file.
     * @param path - The path to the file
     * @return - The type a file is, example a john.js file would return js
     */
	public static String getFileExt(final String path) {
		int slashIndex = path.lastIndexOf(ServerConstant.FORWARD_SINGLE_SLASH);
		String basename = (slashIndex < 0) ? path : path.substring(slashIndex + 1);

		int dotIndex = basename.lastIndexOf('.');
		if (dotIndex >= 0) {
			return basename.substring(dotIndex + 1);
		} else {
			return "";
		}
	}

    /**
     * Gets what mime type a file is.
     * @param path - Path to the file
     * @return - The mime type of the file. Example: a john.js file would return application/javascript
     */
	public static String getFileMime(final String path) {
		String ext = getFileExt(path).toLowerCase();

		return ServerConstant.MIME_MAP.getOrDefault(ext, ServerConstant.APPLICATION_OCTET_STREAM);
	}
}
