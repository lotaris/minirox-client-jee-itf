package com.lotaris.minirox.client.j2ee.itf;

import com.lotaris.j2ee.itf.model.Description;
import com.lotaris.rox.annotations.RoxableTest;
import com.lotaris.rox.annotations.RoxableTestClass;
import com.lotaris.minirox.client.MiniRoxConfiguration;
import com.lotaris.rox.client.j2ee.itf.AbstractRoxListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listener to send results to MiniROX Agent. This listener is a wrapping
 * of the {@link com.lotaris.minirox.MiniRoxListener}
 * 
 * @author Laurent Prevost, laurent.prevost@lotaris.com
 */
public class MiniRoxListener extends AbstractRoxListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(MiniRoxListener.class);
	
	/**
	 * MiniROX configuration
	 */
	private static final MiniRoxConfiguration miniRoxConfiguration = MiniRoxConfiguration.getInstance();
	
	/**
	 * Keep the project as it is not present in ROX directly
	 */
	private String projectName;
	
	/**
	 * MiniROX wrapped listener
	 */
	private com.lotaris.minirox.client.MiniRoxListener wrappedListener = new com.lotaris.minirox.client.MiniRoxListener();

	public MiniRoxListener() {}
	
	public MiniRoxListener(String category, String projectName) {
		super(category);
		this.projectName = projectName;
	}
	
	@Override
	public void testRunStart() {
		super.testRunStart();
		
		if (!miniRoxConfiguration.isEnabled()) {	
			return;
		}

		wrappedListener.testRunStart(
			projectName, 
			configuration.getProjectVersion(), 
			getCategory(null, null, null)
		);
	}
	
	@Override
	public void testRunEnd() {
		super.testRunEnd();
		
		if (!miniRoxConfiguration.isEnabled()) {	
			return;
		}

		wrappedListener.testRunEnd(
			projectName, 
			configuration.getProjectVersion(), 
			getCategory(null, null, null),
			endDate - startDate
		);
	}

	@Override
	public void testEnd(Description description) {
		super.testEnd(description);

		if (!miniRoxConfiguration.isEnabled()) {	
			return;
		}
		
		RoxableTest methodAnnotation = getMethodAnnotation(description);
		RoxableTestClass cAnnotation = getClassAnnotation(description);

		if (methodAnnotation != null) {
			if (!methodAnnotation.key().isEmpty()) {
				wrappedListener.testResult(
					createTest(description, methodAnnotation, cAnnotation),
					projectName, 
					configuration.getProjectVersion(), 
					getCategory(null, null, null)
				);
			}
			else {
				LOGGER.warn("@{} annotation is present but the key is not configured.", RoxableTest.class.getSimpleName());
			}
		} 
		else {
			LOGGER.warn("@{} annotation is missing on method name: {}", RoxableTest.class.getSimpleName(), description.getName());
		}
	}
}