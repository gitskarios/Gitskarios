$(document).ready(function() {
    $('.carousel').slick({centerMode: true,
  centerPadding: '60px',
  slidesToShow: 3,
  responsive: [
    {
      breakpoint: 768,
      settings: {
        arrows: false,
        centerMode: true,
        centerPadding: '40px',
        slidesToShow: 3
      }
    },
    {
      breakpoint: 480,
      settings: {
        arrows: false,
        centerMode: true,
        centerPadding: '40px',
        slidesToShow: 1
      }
    }
  ]});
    $.ajax({
        url: 'https://api.github.com/repos/alorma/Gitskarios',
        dataType: 'jsonp',
        method: 'get',
        success: function(data) {
            $('#star-count').html(data.data.stargazers_count);
            $('#follow-count').html(data.data.watchers_count);
        }
    });
});
