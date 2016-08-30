package rpc.common;

public class RpcResponse {

	private String requestId;
    private Throwable error;
    private Object result;
	/**
	 * @return the requestId
	 */
	public String getRequestId() {
		return requestId;
	}
	/**
	 * @param requestId the requestId to set
	 */
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	/**
	 * @return the error
	 */
	public Throwable getError() {
		return error;
	}
	/**
	 * @param error the error to set
	 */
	public void setError(Throwable error) {
		this.error = error;
	}
	/**
	 * @return the result
	 */
	public Object getResult() {
		return result;
	}
	/**
	 * @param result the result to set
	 */
	public void setResult(Object result) {
		this.result = result;
	}
	/**
	 * 是否存在异常
	 * @return
	 */
	public boolean isError() {
		return this.error != null;
	}
}
