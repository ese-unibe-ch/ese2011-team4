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
		var val = $('#invitations').val().trim();
		var contacts = (val.length>0)?val.split(","):new Array();
		var current = $(this).attr("name");
		if($(this).is(':checked')) {
			contacts.push(current);
		} else {
			for(i=0; i < contacts.length; i++) {
				contacts[i] = contacts[i].trim();
				if(contacts[i] == current) {
					contacts.splice(i,1);
					i--;
				}
			}
		}
		
		var str = contacts.join(", ");
		
		$('#invitations').val(str);
	});
	
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
	
	$.datepicker.setDefaults(
  		$.extend(
    		{'dateFormat':'dd.mm.yyyy'},
    		$.datepicker.regional['de-CH']
  		)
	);

	$(function() {
		$(".datepicker").datepicker();
	});
}
