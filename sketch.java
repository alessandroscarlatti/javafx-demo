new Wizard(wizard -> {
	wizard.setTitle("some title");
	wizard.setSize(300, 400);
	wizard.setInternalIcon(someImage);
	
	// wizard variable is a widget.
	// that means it will not actually be a direct swing component.
	// various widget components can be made accessible by the widget.
	wizard.getJPanel().setToolTipText("asdf");
	
	widget.configureGrid(grid -> {
		grid.addRow(row -> {
			row.addCell(cell -> {
				cell.addSwingComponent(() -> {
					JLabel jLabel = new JLabel();
					jLabel.setText("Asdf");
					return jLabel;
				});
			});
			row.addCell(cell -> {
				cell.addTextField(userTextField -> {
					userTextField.onChange(this::onChangeUser);
				});
			});
		});
		grid.addRow(row -> {
			row.addCell(cell -> {
				cell.addLabel("password");
			});
			row.addCell(cell -> {
				cell.addPasswordField(userPasswordField -> {
					userPasswordField.onChange(this::onChangeUser);
				});
			});
		});
	});
	
	
	wizard.setFeaturedContentPanel(new Grid(grid -> {
		
	}));
	
	wizard.configureBottomActionToolbar(toolbar -> {
		toolbar.addButton(button -> {
			button.setText("Back");
			button.onClick(this::onClickBack);
		});
		
		toolbar.addButton(button -> {
			button.setText("Next");
			button.onClick(this::onClickNext);
		});
		
		toolbar.addButton(new JButton(), button -> {
			
		});
	});
});

SwingNode.create(new JPanel(), jPanel -> {
	jPanel.setToolTipText("asdf");
	jPanel.add(SwingNode.create(new JButton(), button -> {
		
	}));
});

Widget.create(new CoolWidget(), widget -> {
	widget.doSomethingForThisWidget();
});


WizardWidget wizardWidget = new WizardWidget(wizard -> {
	wizard.icon = new Image("asdf");
	wizard.scrollX = true;
	wizard.scrollY = true;
	wizard.content = new FileChooserWidget(fileChooser -> {
		fileChooser.mode = OPEN;
		fileChooser.initialFile = Paths.get("asdf");
		fileChooser.getFileStrategy
	});
})

WizardWidget wizardWidget = new WizardWidget(wizard -> {
	wizard.setIcon(new Image("asdf"));
	wizard.setScrollX(true);
	wizard.setScrollY(true);
	wizard.content = new FileChooserWidget(fileChooser -> {
		fileChooser.mode = OPEN;
		fileChooser.initialFile = Paths.get("asdf");
		fileChooser.getFileStrategy
	});
})

provide:
-----------------
- empty constructor
- constructor with props
- constructor with props config
- getter for props
- use public properties for now
- use props as an inner class


 return WizardWidget.ui(wizard -> {
	wizard.wizardContent = RowsWidget.ui(rowsWidget -> {
		rowsWidget.addRow(MultilineTextWidget.ui(
			"This is a really dangerous task that has a really long description.\n" +
				"It may take as long to execute this task as it does to read about it."
		));
		rowsWidget.addRow(ProgressBarWidget.ui((progressBarWidget -> {
			progressBarWidget.setRepeatable(true);
			progressBarWidget.getProgressBarTemplate().setWork(() -> {
				sleep(3000);
			});
		})));
	});
	wizard.title = "Dangerous task.";
});

flow.executeStep(step -> {
	step.setName("config1");
	step.setRepeatable(true);
	step.setGoNextText("Install Files");
	step.setExecution(() -> {
		card.setView(config1Card);
		
		// when this method finishes, that means the step is done.
		flow.canGoNext(true);
	});
});

flow.executeStep(step -> {
	step.setName("task1");
	step.setExecution(() -> {
		// update whatever view necessary
		card.setView(task1Card);
		
		// now start the task.
		// exceptions will be thrown here if the work fails.
		try {
			task1.getProgressBarWidget().getProgressBarTemplate().invokeWorkWithProgressBar();
		} catch (Exception e) {
			// decide the next step...
			// trigger the next step...
		}
	});
});

flow.executeStep(step -> {
	step.setName("config2");
	step.setRepeatable(true);
	step.setGoNextText("Install Files 2");
	step.setExecution(() -> {
		card.setView(config1Card);
		
		// when this method finishes, that means the step is done.
		flow.canGoNext(true);
	});
});

flow.executeStep(step -> {
	step.setName("task2");
	step.setExecution(() -> {
		// update whatever view necessary
		card.setView(task1Card);
		
		// now start the task.
		// exceptions will be thrown here if the work fails.
		task1.getProgressBarWidget().getProgressBarTemplate().invokeWorkWithProgressBar();
	});
});





