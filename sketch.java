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








