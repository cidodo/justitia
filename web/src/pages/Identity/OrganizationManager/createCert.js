import React, { Component, Fragment } from 'react';
import { Icon, Select, Form, Input, Button } from 'antd';
import { connect } from 'dva';
import { toFile } from '@/utils/utils';

const FormItem = Form.Item;
const Option = Select.Option;
const TextArea = Input.TextArea;

@connect(({ organizationManager }) => {
  return {
    organizationManager
  };
})
class CreateCert extends Component {
  constructor(props) {
    super(props);
    this.downloadEle = React.createRef();
  }
  componentDidUpdate(prevProps) {
    const { organizationManager } = this.props;
    const { isCreate, memberFile } = organizationManager;
    const prevCreate = prevProps.organizationManager.isCreate;
    if (!isCreate && prevCreate && memberFile) {
      const params = {
        ele: this.downloadEle.current,
        fileName: `不动产证书文件.zip`,
        object: memberFile
      };
      toFile(params);
    }
  }
  // 提交表单
  handleSubmit = (e) => {
    e.preventDefault();
    const { form, dispatch } = this.props;

    form.validateFields((err, values) => {
      if (err) return false;
      dispatch({
        type: 'organizationManager/handleCreateMember',
        payload: values
      });
    });
  }

  render() {
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
    const { form, organizationManager, dataChannel } = this.props;
    const { isCreate } = organizationManager;
    const { getFieldDecorator } = form;

    return (
      <Form onSubmit={this.handleSubmit}>
        <FormItem {...formItemLayout} hasFeedback={true} label={"组织名称"}>
          {getFieldDecorator('orgName', {
            rules: [{
              required: true,
              message: '请输入被增加组织的名称！',
            },
            {
              pattern: /^[a-zA-Z][0-9a-zA-Z]{1,128}$/g,
              message: ''
            }],
          })(
            <Input placeholder="被增加组织的名称！" />
          )}
        </FormItem>

        <FormItem {...formItemLayout} hasFeedback={true} label={"组织MSPID"}>
          {getFieldDecorator('orgMspId', {
            rules: [{
              required: true,
              message: '请输入被增加组织的MSPID！',
            },
            {
              pattern: /^[a-zA-Z][0-9a-zA-Z]{1,128}$/g,
              message: ''
            }],
          })(
            <Input placeholder="被输入组织MSPId！" />
          )}
        </FormItem>
        <FormItem {...formItemLayout} hasFeedback={true} label={"组织身份"}>
          {getFieldDecorator('identity', {
            rules: [
              {
                required: true,
                message: '请选择组织身份！',
              }
            ],
          })(
            <Select>
              <Option value="land">land</Option>
              <Option value="bank">bank</Option>
              <Option value="agency">agency</Option>
            </Select>
          )}
        </FormItem>
        <FormItem {...formItemLayout} hasFeedback={true} label={"fabric用户数量"}>
          {getFieldDecorator('userCount', {
            initialValue: 3,
            rules: [
              {
                required: true,
                message: '请输入fabric用户的数量！',
              }, {
                pattern: /^[0-9]{1,10}$/,
                message: '请输入数字'
              }
            ],
          })(
            <Input placeholder="fabric用户的数量！" />
          )}
        </FormItem>
        <FormItem {...formItemLayout} hasFeedback={true} label={"peer节点数量"}>
          {getFieldDecorator('peerCount', {
            initialValue: 3,
            rules: [
              {
                required: true,
                message: '请输入peer节点的数量！',
              }, {
                pattern: /^[0-9]{1,10}$/,
                message: '请输入数字'
              }
            ],
          })(
            <Input placeholder="peer节点的数量！" />
          )}
        </FormItem>
        <FormItem wrapperCol={{ span: 8, offset: 8 }}>
          <Button type="primary" block htmlType="submit" loading={isCreate}>确定</Button>
        </FormItem>
        <div ref={this.downloadEle}></div>
      </Form>
    )
  }
}

export default Form.create()(CreateCert);
