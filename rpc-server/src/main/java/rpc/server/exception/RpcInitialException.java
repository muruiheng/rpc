package rpc.server.exception;

public class RpcInitialException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4313915858274983232L;

	public RpcInitialException() {
		super();
	}

	public RpcInitialException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public RpcInitialException(String message, Throwable cause) {
		super(message, cause);
	}

	public RpcInitialException(String message) {
		super(message);
	}

	public RpcInitialException(Throwable cause) {
		super(cause);
	}

	
	
}
