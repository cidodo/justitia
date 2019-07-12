import React, { Component } from 'react';
import { Form, Button, Input, Select } from 'antd';
import { connect } from 'dva';

const FormItem = Form.Item;
const Option = Select.Option;

@connect(({ ChannelManager, network }) => {
  return {
    ChannelManager,
    network
  };
})
class JoinChannel extends Component {
  
  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'ChannelManager/handleGetPeerList'
    })
  }
  

  // 加入通道
  handleJoinChannel = (e) => {
    e.preventDefault();
    const { form, dispatch } = this.props;
    form.validateFields((err, values) => {
      if (err) return false;
      dispatch({
        type: 'ChannelManager/handleJoinChannel',
        payload: values
      });
    });
  }

  render() {
    const { form, ChannelManager } = this.props;
    const { peerList, isJoin } = ChannelManager;
    const { getFieldDecorator } = form;
    // Form表单layout
    const formItemLayout = {
      labelCol: {
        xs: { span: 24 },
        sm: { span: 8 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 8 },
      },
    };

    return (
      <Form onSubmit={this.handleJoinChannel}>
        <FormItem {...formItemLayout} hasFeedback={true} label={"通道名称"}>
          {getFieldDecorator('channelId', {
            rules: [{
              required: true,
              message: '请填写通道名称！',
            },
            {
              pattern: /^[a-z]\S{0,128}$/g,
              message: '请输入第一个字符为小写字母且长度不超过128位的名称!',
            }],
          })(
            <Input placeholder="请填写新增通道名称！" />
          )}
        </FormItem>
        <FormItem {...formItemLayout} hasFeedback={true} label={"peer节点名称"}>
          {getFieldDecorator('peersId', {
            rules: [{
              required: true,
              message: '请填写peer节点名称！',
            }],
          })(
            <Select mode="multiple">
              {peerList.map((ele, index) => (
                <Option value={ele.id} key={index}>{ele.id}</Option>
              ))
              }
            </Select>
          )}
        </FormItem>
        <FormItem wrapperCol={{ span: 8, offset: 8 }}>
          <Button type="primary" block htmlType="submit" loading={isJoin}>确认</Button>
        </FormItem>
      </Form>
    )
  }
}

export default Form.create()(JoinChannel);