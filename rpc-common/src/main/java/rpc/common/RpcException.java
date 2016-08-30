package rpc.common;


/**
 * RpcException 异常
 * @author mrh
 *
 */
public class RpcException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5542427270449982839L;

	public RpcException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RpcException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public RpcException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public RpcException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public RpcException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
}
