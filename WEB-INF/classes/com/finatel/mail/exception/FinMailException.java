/**
 * 
 */
package com.finatel.mail.exception;

/**
 * @author ftuser
 *
 */
public class FinMailException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1201260598862929543L;

	/**
	 * 
	 */
	public FinMailException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public FinMailException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public FinMailException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public FinMailException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
