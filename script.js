(function() {
    const loader = document.getElementById("loader");
    let dotLength = 0;

    const intervalId = setInterval(() => {
        if (dotLength === 3) dotLength = 0;
        else dotLength++;
        loader.innerHTML = "LOADING" + ".".repeat(dotLength);
    }, 200);

    window.stopLoader = () => {
        clearInterval(intervalId);
        loader?.remove();
    }
})();
