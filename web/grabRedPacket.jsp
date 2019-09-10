<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
    <title>Title</title>
    <script src="https://code.jquery.com/jquery-3.2.1.js"></script>
    <script type="text/javascript">
        $(document).ready(function(){
          // 模拟30000个异步请求，进行并发
            var max= 3000;
            for(var i = 1; i <=max; i++)
            {
                $.post({
                    url:"./userRedPacket/grapRedPacket?redPacketId=1&uesrId=" + i,
                    success:function (result) {

                    }

                });
            }
        });
    </script>
</head>
<body>

</body>
</html>
