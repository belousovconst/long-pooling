function comet() {
    $.ajax({
        url: "http://localhost:8080/greeting?timeout=10000&token=1"
    }).then(function(data, status, jqxhr) {
       $('.greeting-id').append(data.id);
       $('.greeting-content').append(data.content);
       console.log(jqxhr);
       console.log('status = ' + status);
    });
}

$(document).ready(comet());

var timerId = setInterval(comet, 10000);

// через 5 сек остановить повторы
setTimeout(function() {
  clearInterval(timerId);
}, 60000);





