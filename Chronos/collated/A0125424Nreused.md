# A0125424Nreused
###### src\gui\GUI.java
``` java
	private void registerKeyboard() {
		 try {
	        	GlobalScreen.registerNativeHook();
	        	turnOffKeyboardLog();
	        }
	        catch (NativeHookException ex) {
	           log.warning(MESSAGE_REGISTER_NATIVEHOOK_FAIL);
	           handleCommand(CLOSE_SYSTEM);
	        }

	        GlobalScreen.addNativeKeyListener(new GUI());
	    }

```
###### src\gui\GUI.java
``` java
    private void turnOffKeyboardLog() {
    	LogManager.getLogManager().reset();
    	keyboardLogger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
    	keyboardLogger.setLevel(Level.OFF);
	}

```
###### src\gui\GUI.java
``` java
	/**
	 * This method creates a tray and subsequently a tray icon for the
	 * application.
	 * 
	 * @param stage
	 * @param scene
	 */
	private void createTray() {
		if (SystemTray.isSupported()) {
			tray = SystemTray.getSystemTray();
			ImageIcon image = null;
			image = new ImageIcon(getClass().getResource("/gui/logo.jpg"));

			trayIcon = new TrayIcon(image.getImage());

			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				log.warning(MESSAGE_TRAYICON_FAIL);
			}
		}
	}

```
