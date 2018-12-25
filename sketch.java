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











