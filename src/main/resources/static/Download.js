function download()
{
    try
    {
    const link = document.getElementById("link").value;
    if(link === "")
    {
        document.getElementById("status").textContent = 'please input link';
        return;
    }
    const params = new URLSearchParams({link: link})
    const url = `http://localhost:8080/Magpie/downloadVideo?${params.toString()}`
    window.location.href = url;
    document.getElementById("link").value = '';
    document.getElementById("status").innerHTML = '\t wait for it<br>it may take some time...';
    }
    catch(error)
    {
        document.getElementById("status").innerHTML = 'we apologise. an error has occured';
    }
}