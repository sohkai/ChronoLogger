$("#loginButton").click(function(){
	var data = {
		mail: $('#mf').val(),
		password: $('#pf').val(),
	};

	request = $.ajax({
		url: '/loginprocess',
		type: "post",
		data: data,
	});

	request.success(function (response, textStatus, jqXHR){
		window.location.assign("/");
	});

	request.fail(function (jqXHR, textStatus, errorThrown){
		// Set the error label
		$("#errorLabel").show();
	});
});

$(".form-control").focus(function() {
	$("#errorLabel").hide();
});
