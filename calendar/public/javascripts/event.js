function setupCalendar(comments) {
	$(".participation-details").ready(function() {
		$('.participation-details').hide();
	});
	
	$(".expand").click(function(event) {
		event.preventDefault();
		$(this).parent().siblings(".participation-details").slideToggle('slow');
	});
	if(comments != true) {
		$(".comments").ready(function() {
			$('.comments').hide();
		});
		
		$(".show-comments").click(function(event) {
			event.preventDefault();
			$(this).parent().siblings(".comments").slideToggle('slow');
		});
	}
}

function setupEventForm() {
	$('#contact-button').ready(function() {
		$('#contact-list').hide();
	});
	
	$('#contact-button').click(function(event) {
		$('#contact-list').slideToggle('slow');
	});
	
	$('.contact-check').change(function() {
		var contacts = $('#invitations').val().split(",");
		var current = $(this).attr("name");
		if($(this).is(':checked')) {
			contacts[contacts.length] = current;
		} else {
			for(i=0; i < contacts.length; i++) {
				if(contacts[i].trim() == current) {
					contacts[i] = "";
				}
			}
		}
		
		var str = buildContactString(contacts);		
		
		$('#invitations').val(str);
	});
	
	function buildContactString(contacts) {
		var str = "";
		for(i=0; i < contacts.length; i++) {
			if(contacts[i] != "") {
				str += contacts[i];
				if(i < contacts.length-1) {
					str += ", ";
				}
			}
		}
		return str;
	}
	
	$('#repeating').ready(function() {
		if($('#repeating option:selected').attr("value") == 'NONE')
		{
			$('#repeating-details').hide();
		}
	});
	
	$('#repeating').change(function() {
		if($('#repeating option:selected').attr("value") == 'NONE')
		{
			$('#repeating-details').hide('slow');
		}
		else
		{
			$('#repeating-details').show('slow');
		}
	});
}