function findBootstrapEnvironment() {
    var envs = ['xs', 'sm', 'md', 'lg'];

    $el = $('<div>');
    $el.appendTo($('body'));

    for (var i = envs.length - 1; i >= 0; i--) {
        var env = envs[i];

        $el.addClass('hidden-' + env);
        if ($el.is(':hidden')) {
            $el.remove();
            return env
        }
    };
}
$(document).ready(function () {
    if (findBootstrapEnvironment() === 'xs') {
        $('.carousel').slick({
            centerMode: true,
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
                ]
        });
        $.ajax({
            url: 'https://api.github.com/repos/alorma/Gitskarios',
            dataType: 'jsonp',
            method: 'get',
            success: function (data) {
                $('#star-count').html(data.data.stargazers_count);
                $('#follow-count').html(data.data.watchers_count);
            }
        });
    }
});
