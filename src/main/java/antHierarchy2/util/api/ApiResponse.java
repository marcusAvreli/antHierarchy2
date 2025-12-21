package antHierarchy2.util.api;





public class ApiResponse<T> {

    private int httpCode;
    private String httpMessage;

    private Long  code;
    private String message;

    private T data;

    public ApiResponse() {}

    public ApiResponse(int httpCode, String httpMessage,StatusCode errorCode, T data) {
        this.httpCode = httpCode;
        this.httpMessage = httpMessage;
      this.code = errorCode.getErrorCode();
        this.message = errorCode.getErrorMessage();
        this.data = data;
    }

    // Success helper
    public static <T> ApiResponse<T> success(T data) {
        StatusCode successCode = new StatusCode();
        successCode.setErrorCode(0L);
        successCode.setErrorMessage("Success");
        return new ApiResponse<T>(200, "OK", successCode, data);
    }

    public int getHttpCode() {
		return httpCode;
	}

	public void setHttpCode(int httpCode) {
		this.httpCode = httpCode;
	}

	public String getHttpMessage() {
		return httpMessage;
	}

	public void setHttpMessage(String httpMessage) {
		this.httpMessage = httpMessage;
	}

	public Long getCode() {
		return code;
	}

	public void setCode(Long code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	// Error helper with dynamic code
    public static <T> ApiResponse<T> error(int httpCode, String httpMessage, StatusCode errorCode) {
        return new ApiResponse<T>(httpCode, httpMessage, errorCode, null);
    }

    // Getters & setters...
}
