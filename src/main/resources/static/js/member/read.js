
new Swiper('.images . swiper-container', {
    slidesPerView: 2, // 한번에 보여줄 슬라이드 개수
    spaceBetween: 10, // 슬라이드 사이 여백
    centeredSlides: true, // 1번 슬라이드가 가운데 보이기
    loop: true,
    autoplay: {
        delay: 5000
    },
    navigation: {
        prevEl: '.images .swiper-button-prev ',
        nextEl: '.images .swiper-button-next ',
    }
});